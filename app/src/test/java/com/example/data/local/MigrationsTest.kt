package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MigrationsTest {

    private data class ColumnDef(val type: String, val notNull: Int, val pk: Int)

    private lateinit var context: Context
    private val dbName = "migration_probe.db"

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(dbName)
    }

    @After
    fun teardown() {
        context.deleteDatabase(dbName)
    }

    private fun createV6Helper(onCreate: (SupportSQLiteDatabase) -> Unit = {}): SupportSQLiteOpenHelper {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(6) {
                override fun onCreate(db: SupportSQLiteDatabase) = onCreate(db)
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()
        return FrameworkSQLiteOpenHelperFactory().create(configuration)
    }

    private fun openRoomGeneratedSchema(): Map<String, Map<String, ColumnDef>> {
        val room = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            return schemaOf(room.openHelper.writableDatabase)
        } finally {
            room.close()
        }
    }

    private fun schemaOf(db: SupportSQLiteDatabase): Map<String, Map<String, ColumnDef>> {
        val schema = mutableMapOf<String, Map<String, ColumnDef>>()
        for (table in tableNames(db)) {
            val columns = mutableMapOf<String, ColumnDef>()
            db.query("PRAGMA table_info(`$table`)").use { cursor ->
                val nameIx = cursor.getColumnIndexOrThrow("name")
                val typeIx = cursor.getColumnIndexOrThrow("type")
                val notNullIx = cursor.getColumnIndexOrThrow("notnull")
                val pkIx = cursor.getColumnIndexOrThrow("pk")
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIx)
                    columns[name] = ColumnDef(
                        type = cursor.getString(typeIx),
                        notNull = cursor.getInt(notNullIx),
                        pk = cursor.getInt(pkIx)
                    )
                }
            }
            schema[table] = columns
        }
        return schema
    }

    private fun tableNames(db: SupportSQLiteDatabase): List<String> {
        val names = mutableListOf<String>()
        db.query("SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name").use { cursor ->
            while (cursor.moveToNext()) names.add(cursor.getString(0))
        }
        return names.filterNot { it in IGNORED_TABLES }
    }

    private fun userVersion(db: SupportSQLiteDatabase): Int {
        db.query("PRAGMA user_version").use { cursor ->
            cursor.moveToFirst()
            return cursor.getInt(0)
        }
    }

    private fun assertSchemasMatch(
        expected: Map<String, Map<String, ColumnDef>>,
        actual: Map<String, Map<String, ColumnDef>>
    ) {
        assertEquals(
            "table sets differ",
            expected.keys.sorted(),
            actual.keys.sorted()
        )
        for ((table, expectedColumns) in expected) {
            val actualColumns = actual.getValue(table)
            assertEquals("columns of $table", expectedColumns.keys.sorted(), actualColumns.keys.sorted())
            for ((column, expectedDef) in expectedColumns) {
                val actualDef = actualColumns.getValue(column)
                assertEquals("$table.$column type", expectedDef.type, actualDef.type)
                assertEquals("$table.$column notNull", expectedDef.notNull, actualDef.notNull)
                assertEquals("$table.$column primaryKey", expectedDef.pk, actualDef.pk)
            }
        }
    }

    @Test
    fun migrationTargetsVersion6To7() {
        assertEquals(6, MIGRATION_6_7.startVersion)
        assertEquals(7, MIGRATION_6_7.endVersion)
    }

    @Test
    fun migratedSchemaMatchesRoomGeneratedSchema() {
        val helper = createV6Helper()
        val migrated = helper.writableDatabase
        try {
            MIGRATION_6_7.migrate(migrated)
            val migratedSchema = schemaOf(migrated)
            val generatedSchema = openRoomGeneratedSchema()

            assertEquals("entity count", 34, generatedSchema.size)
            assertSchemasMatch(generatedSchema, migratedSchema)
        } finally {
            helper.close()
        }
    }

    @Test
    fun migrationIsIdempotent() {
        val helper = createV6Helper()
        val migrated = helper.writableDatabase
        try {
            MIGRATION_6_7.migrate(migrated)
            val firstRun = schemaOf(migrated)

            MIGRATION_6_7.migrate(migrated)
            val secondRun = schemaOf(migrated)

            assertSchemasMatch(firstRun, secondRun)
        } finally {
            helper.close()
        }
    }

    @Test
    fun migrationKeepsRowsOfTablesThatAlreadyExist() {
        val helper = createV6Helper()
        try {
            val db = helper.writableDatabase
            MIGRATION_6_7.migrate(db)
            db.execSQL(
                "INSERT INTO bookings (id, rentalItemId, rentalItemTitle, rentalItemCategory, " +
                    "pricePerDay, days, totalPrice, paymentMethod, paymentPhone) " +
                    "VALUES (42, 7, 'Villa Sablière', 'Immobilier', 50000, 2, 100000, 'Airtel Money', '070000000')"
            )

            MIGRATION_6_7.migrate(db)

            db.query("SELECT COUNT(*) FROM bookings WHERE id = 42").use { cursor ->
                cursor.moveToFirst()
                assertEquals("pre-existing booking must survive a repeated migration", 1, cursor.getInt(0))
            }
        } finally {
            helper.close()
        }
    }

    @Test
    fun roomOpensVersion6DatabaseAppliesMigrationAndValidatesSchema() = runTest {
        val helper = createV6Helper { db -> MIGRATION_6_7.migrate(db) }
        helper.writableDatabase.use { db ->
            assertEquals(6, userVersion(db))
            db.execSQL(
                "INSERT INTO bookings (id, rentalItemId, rentalItemTitle, rentalItemCategory, " +
                    "pricePerDay, days, totalPrice, paymentMethod, paymentPhone) " +
                    "VALUES (1, 1, 'Villa Existante', 'Immobilier', 75000, 3, 225000, 'Moov Money', '060000000')"
            )
        }
        helper.close()

        val room = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(MIGRATION_6_7)
            .allowMainThreadQueries()
            .build()
        try {
            val bookings = room.rentalDao().getAllBookings().first()
            assertEquals("data must survive the migration", 1, bookings.size)
            assertEquals("Villa Existante", bookings[0].rentalItemTitle)
            assertEquals(225000, bookings[0].totalPrice)

            val items = room.rentalDao().getAllRentalItems().first()
            assertNotNull(items)
            assertTrue(items.isEmpty())

            assertEquals("user_version must be bumped to 7", 7, userVersion(room.openHelper.writableDatabase))
            assertTrue(tableNames(room.openHelper.writableDatabase).containsAll(EXPECTED_TABLES))
        } finally {
            room.close()
        }
    }

    private companion object {
        val IGNORED_TABLES = setOf("android_metadata", "room_master_table", "sqlite_sequence")

        val EXPECTED_TABLES = listOf(
            "rental_items", "bookings", "chat_messages", "user_profile", "search_history",
            "notifications", "disputes", "earnings", "reviews", "payment_history",
            "saved_searches", "search_suggestions", "voice_search_history", "owner_analytics",
            "market_insights", "push_notification_settings", "referral_tracking", "user_follows",
            "verification_badges", "community_disputes", "neighborhood_reviews", "booking_escrows",
            "split_payments", "payment_reminders", "payment_receipts", "calendar_syncs",
            "media_items", "media_upload_settings", "insurance_claims", "insurance_subscriptions",
            "wallet", "wallet_transactions", "payment_methods_local", "promo_codes"
        )
    }
}
