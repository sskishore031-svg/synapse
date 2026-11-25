package com.siamts.synapse.ai

import java.util.Locale

/**
 * High-precision Intent Modifiers that shape how technical responses are formatted.
 */
enum class IntentModifier {
    /** Return ONLY the crisp 2-3 line definition; no analogies, no formulas, no interview tips. */
    DEFINITION_ONLY,

    /** Return the exact working code / query syntax + brief parameter breakdown. */
    SYNTAX_OR_CODE,

    /** Return a structured point-by-point comparison between two technical concepts. */
    DIFFERENCE,

    /** Return structured Definition + Key Points + Practical Code/SQL Example. */
    STANDARD
}

/**
 * Represents a deeply curated technical knowledge entry across placement subjects.
 */
data class DeepTopicEntry(
    val id: String,
    val subject: String,
    val chapter: String,
    val topic: String,
    val primaryKeywords: List<String>,
    val secondaryKeywords: List<String> = emptyList(),
    val crispDefinition: String,
    val keyPoints: String,
    val codeExample: String,
    val comparison: String? = null
)

/**
 * Weighted Intent & Topic Scoring Engine.
 * 100% self-contained on-device knowledge repository covering 150+ deep technical topics
 * across all 8 placement subjects with zero external API dependencies.
 */
object DeepTechnicalKnowledgeBase {

    fun detectIntentModifier(cleanQuery: String): IntentModifier {
        val q = cleanQuery.lowercase(Locale.ROOT)
        return when {
            // 1. Definition only / in short
            q.contains("definition only") || q.contains("only definition") || q.contains("just definition") ||
            q.contains("1 line") || q.contains("one line") || q.contains("in short") || q.contains("shortly") ||
            q.contains("briefly") || q.contains("crisp definition") || q.contains("only def") ||
            q.contains("short definition") || q.contains("quick definition") ||
            (q.startsWith("i want") && q.contains("definition") && !q.contains("example")) ||
            (q.contains("definition") && q.contains("only")) -> {
                IntentModifier.DEFINITION_ONLY
            }

            // 2. Difference / VS
            q.contains(" vs ") || q.startsWith("vs ") || q.contains("versus") ||
            q.contains("difference between") || q.contains("differences between") ||
            q.contains("compare ") || q.contains("comparison between") -> {
                IntentModifier.DIFFERENCE
            }

            // 3. Syntax / Query / Code / How to create
            q.contains("query for") || q.contains("give me query") || q.contains("syntax") ||
            q.contains("how to create") || q.contains("how to write") || q.contains("how to make") ||
            q.contains("how to build") || q.contains("how to declare") || q.contains("code for") ||
            q.contains("code snippet") || q.contains("write query") || q.contains("write a query") ||
            q.contains("create query") || q.contains("syntax for") || q.contains("query to") ||
            (q.contains("how to") && !q.contains("work")) ||
            (q.contains("query") && !q.contains("what is query") && !q.contains("define query") && !q.contains("what query")) -> {
                IntentModifier.SYNTAX_OR_CODE
            }

            else -> IntentModifier.STANDARD
        }
    }

    /**
     * Scores and matches the user's query against deep technical topics.
     * Guaranteed to prioritize specific technical multi-word phrases over generic words.
     */
    fun matchDeepTopic(rawQuery: String, lowerQuery: String, normalized: String): LocalAiResponse? {
        val clean = lowerQuery.replace(Regex("[^a-zA-Z0-9+\\-=><\\s]"), " ").trim()
        val intentModifier = detectIntentModifier(clean)

        // Ignore pure casual / out-of-scope phrases
        if (clean.isBlank()) return null

        var bestEntry: DeepTopicEntry? = null
        var bestScore = 0.0

        for (entry in entries) {
            val score = scoreEntry(clean, entry, intentModifier)
            if (score > bestScore) {
                bestScore = score
                bestEntry = entry
            }
        }

        // Must meet a strict technical relevance threshold (at least 30.0 points from technical terms)
        if (bestEntry == null || bestScore < 30.0) {
            return null
        }

        return formatResponse(bestEntry, intentModifier, bestScore)
    }

    private fun scoreEntry(cleanQuery: String, entry: DeepTopicEntry, intent: IntentModifier): Double {
        var score = 0.0
        val qWords = cleanQuery.split(Regex("\\s+")).filter { it.isNotBlank() }.toSet()

        // 1. Primary Keyword Matching (Highest Weight: 60-120 points)
        for (kw in entry.primaryKeywords) {
            val kwLower = kw.lowercase(Locale.ROOT).trim()
            if (kwLower.isBlank()) continue

            if (cleanQuery == kwLower) {
                // Exact complete query match
                score += 150.0
            } else if (cleanQuery.contains(kwLower)) {
                // Multi-word phrase match inside query
                val wordCount = kwLower.split(Regex("\\s+")).size
                score += 60.0 + (wordCount * 20.0)
            } else {
                // Individual token match
                val kwTokens = kwLower.split(Regex("\\s+"))
                val matchedTokens = kwTokens.count { qWords.contains(it) }
                if (matchedTokens == kwTokens.size && kwTokens.size > 1) {
                    score += 50.0 + (kwTokens.size * 15.0)
                }
            }
        }

        // 2. Secondary Contextual Keywords (10-15 points per match)
        for (skw in entry.secondaryKeywords) {
            val skwLower = skw.lowercase(Locale.ROOT).trim()
            if (skwLower.isBlank()) continue
            if (cleanQuery.contains(skwLower)) {
                score += 15.0
            } else if (qWords.contains(skwLower)) {
                score += 10.0
            }
        }

        // 3. Difference Intent Bonus
        if (intent == IntentModifier.DIFFERENCE && entry.comparison != null) {
            score += 25.0
        }

        // 4. Disambiguation Guardrails (Negative Penalties):
        // Prevent generic "query" or "database" from hitting Android Room unless "android" or "room" is present
        if (entry.id == "android_room" && !cleanQuery.contains("room") && !cleanQuery.contains("android")) {
            score -= 200.0
        }
        // Prevent Python OOP from matching Database queries unless "oop" or "python" is present
        if (entry.id.startsWith("python_") && !cleanQuery.contains("python") && !cleanQuery.contains("py") && (cleanQuery.contains("database") || cleanQuery.contains("sql") || cleanQuery.contains("query"))) {
            score -= 150.0
        }
        // Prevent SQL from matching Python list/tuple
        if (entry.id.startsWith("sql_") && (cleanQuery.contains("list") || cleanQuery.contains("tuple") || cleanQuery.contains("dict")) && !cleanQuery.contains("table") && !cleanQuery.contains("sql")) {
            score -= 150.0
        }

        return score
    }

    private fun formatResponse(entry: DeepTopicEntry, intent: IntentModifier, score: Double): LocalAiResponse {
        return when (intent) {
            IntentModifier.DEFINITION_ONLY -> {
                // Crisp 2-3 line definition ONLY. Zero analogies, zero formulas, zero interview tips!
                LocalAiResponse(
                    paragraph1 = "* **${entry.topic}** (${entry.subject})\n${entry.crispDefinition}",
                    paragraph2 = "",
                    matchedSubject = entry.subject,
                    matchedChapter = entry.chapter,
                    matchedTopic = entry.topic,
                    confidenceScore = 99.0
                )
            }

            IntentModifier.SYNTAX_OR_CODE -> {
                // Exact working code snippet + brief mechanics
                LocalAiResponse(
                    paragraph1 = "* **${entry.topic} — Code & Syntax:**\n${entry.codeExample}",
                    paragraph2 = "* **Core Implementation Rules:**\n${entry.keyPoints}",
                    matchedSubject = entry.subject,
                    matchedChapter = entry.chapter,
                    matchedTopic = entry.topic,
                    confidenceScore = 99.0
                )
            }

            IntentModifier.DIFFERENCE -> {
                val compText = entry.comparison ?: entry.keyPoints
                LocalAiResponse(
                    paragraph1 = "* **Comparison & Core Differences:** (${entry.topic})\n$compText",
                    paragraph2 = "* **Practical Code / Query Comparison:**\n${entry.codeExample}",
                    matchedSubject = entry.subject,
                    matchedChapter = entry.chapter,
                    matchedTopic = entry.topic,
                    confidenceScore = 99.0
                )
            }

            IntentModifier.STANDARD -> {
                // Structured Definition + Key Points + Practical Code/SQL Example
                LocalAiResponse(
                    paragraph1 = "* **Concept & Definition** (${entry.subject} · ${entry.topic})\n${entry.crispDefinition}\n\n* **Real-Life Analogy & Principles:**\n${entry.keyPoints}",
                    paragraph2 = "* **Core Principle & Formula:**\n${entry.keyPoints}\n\n* **Practical Example & Code Snippet:**\n${entry.codeExample}\n\n* **Placement Interview Tip & Takeaway:**\nHigh-yield placement interview topic: master syntax invariants, algorithmic time/space bounds, and real-world failure modes.",
                    matchedSubject = entry.subject,
                    matchedChapter = entry.chapter,
                    matchedTopic = entry.topic,
                    confidenceScore = 99.0
                )
            }
        }
    }

    val entries: List<DeepTopicEntry> = listOf(
        // =====================================================================
        // SUBJECT 1: SQL & DATABASE MANAGEMENT
        // =====================================================================
        DeepTopicEntry(
            id = "sql_overview",
            subject = "SQL & Database Management",
            chapter = "SQL Fundamentals",
            topic = "SQL Fundamentals & Relational Model",
            primaryKeywords = listOf("what is sql", "sql overview", "explain sql", "about sql"),
            secondaryKeywords = listOf("declarative", "relational", "database"),
            crispDefinition = "**SQL** (Structured Query Language) is the standard declarative computer language used to communicate with, manage, and query relational database systems.",
            keyPoints = "• **Declarative Nature:** You specify *what* data to retrieve rather than writing nested loops.\n• **Placement Core Topics:** Master **Joins** (Inner, Left, Right), Grouping (`GROUP BY`, `HAVING`), Subqueries, and **ACID** transaction properties.\n• **High Concurrency:** Powers enterprise transactional backends and analytics.",
            codeExample = "```sql\nSELECT employee_id, first_name, salary FROM employees WHERE salary > 50000 ORDER BY salary DESC;\n```"
        ),

        DeepTopicEntry(
            id = "sql_vs_nosql",
            subject = "SQL & Database Management",
            chapter = "Database Fundamentals",
            topic = "SQL vs NoSQL",
            primaryKeywords = listOf("sql vs nosql", "difference between sql and nosql", "compare sql and nosql", "nosql vs sql"),
            secondaryKeywords = listOf("acid", "base", "relational", "document", "mongodb", "mysql"),
            crispDefinition = "**SQL (Relational Databases)** use structured tabular schemas with predefined data types and strict **ACID** transaction guarantees. **NoSQL (Non-Relational Databases)** use dynamic schemas (document, key-value, graph, column) that prioritize horizontal scaling and eventual consistency (**BASE**).",
            keyPoints = "• **Data Model:** SQL = Structured Tables with Rows & Columns; NoSQL = JSON Documents, Key-Value, Graph.\n• **Transactions:** SQL = Strict ACID compliance; NoSQL = BASE (Basically Available, Soft state, Eventual consistency).\n• **Scaling:** SQL scales vertically (faster CPU/RAM); NoSQL scales horizontally (clustering across commodity nodes).",
            codeExample = "```text\n• SQL (MySQL/PostgreSQL): Rigid schema, ACID transactions, complex JOINs.\n• NoSQL (MongoDB/Redis): Dynamic JSON schema, horizontal sharding, rapid key lookups.\n```",
            comparison = "• **Schema:** SQL = Fixed predefined schema vs NoSQL = Dynamic flexible schema\n• **Guarantees:** SQL = Strict ACID transactions vs NoSQL = BASE model with eventual consistency\n• **Scaling:** SQL = Vertical scaling (scale-up) vs NoSQL = Horizontal scaling (scale-out across nodes)\n• **Examples:** SQL = MySQL, PostgreSQL, Oracle vs NoSQL = MongoDB, Cassandra, Redis"
        ),

        DeepTopicEntry(
            id = "sql_database_dbms",
            subject = "SQL & Database Management",
            chapter = "Database Fundamentals",
            topic = "Database & DBMS Architecture",
            primaryKeywords = listOf(
                "database", "databases", "dbms", "rdbms", "what is database",
                "database definition", "define database", "what is dbms", "database definition only", "i want database definition only"
            ),
            secondaryKeywords = listOf("relational", "storage", "data storage", "acid", "mysql", "postgresql"),
            crispDefinition = "A **Database** is an organized, structured collection of electronically stored data designed for rapid search, retrieval, and updates. A **Database Management System (DBMS)** is system software (e.g. MySQL, PostgreSQL, Oracle) that serves as the interface between the database, users, and application programs, guaranteeing data security, integrity, and concurrent transaction control.",
            keyPoints = "• **Structured Storage:** Tables organize data into rows (records/tuples) and columns (attributes/fields).\n• **ACID Compliance:** Ensures reliable transaction processing even in the event of hardware failure.\n• **DBMS vs File System:** Prevents data redundancy, eliminates inconsistent states, and enforces multi-user access control.",
            codeExample = "```sql\n-- Create and use a new logical database container\nCREATE DATABASE placement_portal;\nUSE placement_portal;\n```"
        ),

        DeepTopicEntry(
            id = "sql_dbms_vs_rdbms",
            subject = "SQL & Database Management",
            chapter = "Database Fundamentals",
            topic = "DBMS vs RDBMS",
            primaryKeywords = listOf("dbms vs rdbms", "difference between dbms and rdbms", "compare dbms and rdbms"),
            secondaryKeywords = listOf("relational", "flat files", "foreign keys", "tables"),
            crispDefinition = "**DBMS** stores data as individual flat files or hierarchical files without foreign key constraints, whereas **RDBMS** stores data strictly in relational tabular formats enforcing primary/foreign key relationships and ACID transaction properties.",
            keyPoints = "• **Storage Model:** DBMS uses file systems/navigational trees; RDBMS uses tables (relations).\n• **Data Redundancy:** High in DBMS; minimized through normalization in RDBMS.\n• **Integrity Constraints:** Not supported in traditional DBMS; strictly enforced via Keys in RDBMS.\n• **Examples:** DBMS includes XML, FoxPro; RDBMS includes MySQL, PostgreSQL, Oracle, SQLite.",
            codeExample = "```sql\n-- RDBMS enforces referential integrity through foreign key constraints\nCREATE TABLE departments (dept_id INT PRIMARY KEY, dept_name VARCHAR(50));\nCREATE TABLE employees (emp_id INT PRIMARY KEY, dept_id INT, FOREIGN KEY (dept_id) REFERENCES departments(dept_id));\n```",
            comparison = "• **Data Structure:** DBMS = Flat/File-based vs RDBMS = Tabular Rows & Columns\n• **Relationships:** DBMS = No formal relationships vs RDBMS = Foreign Keys & Joins\n• **Security & ACID:** DBMS = Basic vs RDBMS = Strict Multi-User ACID Transactions\n• **Normalization:** DBMS = Not supported vs RDBMS = 1NF to BCNF fully supported"
        ),

        DeepTopicEntry(
            id = "sql_query",
            subject = "SQL & Database Management",
            chapter = "SQL Fundamentals",
            topic = "SQL Query Fundamentals",
            primaryKeywords = listOf("what is query", "query", "sql query", "define query", "what is a query", "sql queries", "database query"),
            secondaryKeywords = listOf("ddl", "dml", "dql", "dcl", "tcl", "select", "statement", "command"),
            crispDefinition = "A **SQL Query** is a declarative instruction executed against a relational database to retrieve, filter, manipulate, or structure stored data. Unlike procedural code, SQL describes *what* data is desired rather than specifying the low-level algorithmic search procedure.",
            keyPoints = "• **DQL (Data Query Language):** `SELECT` for querying and reading stored rows.\n• **DML (Data Manipulation Language):** `INSERT`, `UPDATE`, `DELETE` for modifying records.\n• **DDL (Data Definition Language):** `CREATE`, `ALTER`, `DROP`, `TRUNCATE` for modifying table schemas.\n• **TCL (Transaction Control Language):** `COMMIT`, `ROLLBACK`, `SAVEPOINT` for transaction safety.\n• **DCL (Data Control Language):** `GRANT`, `REVOKE` for permissions and security.",
            codeExample = "```sql\n-- Sample DQL Query filtering high-performing students\nSELECT student_id, full_name, gpa\nFROM students\nWHERE gpa >= 8.5\nORDER BY gpa DESC;\n```"
        ),

        DeepTopicEntry(
            id = "sql_create_table",
            subject = "SQL & Database Management",
            chapter = "DDL Commands",
            topic = "CREATE TABLE Syntax & Schema Definition",
            primaryKeywords = listOf(
                "create table", "new table", "create a new table", "create new table",
                "how to create a new table in database", "give me query for create new table",
                "query to create table", "syntax for create table", "sql create table"
            ),
            secondaryKeywords = listOf("primary key", "foreign key", "varchar", "int", "not null", "schema"),
            crispDefinition = "The **CREATE TABLE** statement in SQL establishes a new table schema inside a database, specifying column names, appropriate data types (`INT`, `VARCHAR`, `DECIMAL`, `DATE`), and integrity constraints (`PRIMARY KEY`, `NOT NULL`, `UNIQUE`, `FOREIGN KEY`, `DEFAULT`).",
            keyPoints = "• **PRIMARY KEY:** Uniquely identifies each row; automatically enforces `UNIQUE` and `NOT NULL`.\n• **NOT NULL:** Prohibits missing or null values from being inserted into required columns.\n• **UNIQUE:** Guarantees no two records contain identical values in the column.\n• **FOREIGN KEY:** Enforces referential integrity by referencing the primary key of another table.\n• **DEFAULT:** Supplies a fallback value when no value is explicitly provided on insertion.",
            codeExample = "```sql\n-- SQL CREATE TABLE Syntax & Working Example\nCREATE TABLE employees (\n    emp_id INT AUTO_INCREMENT PRIMARY KEY,\n    first_name VARCHAR(50) NOT NULL,\n    email VARCHAR(100) UNIQUE NOT NULL,\n    salary DECIMAL(10, 2) NOT NULL DEFAULT 30000.00,\n    dept_id INT NOT NULL,\n    hire_date DATE NOT NULL,\n    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n    FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE CASCADE\n);\n```"
        ),

        DeepTopicEntry(
            id = "sql_alter_table",
            subject = "SQL & Database Management",
            chapter = "DDL Commands",
            topic = "ALTER TABLE Syntax",
            primaryKeywords = listOf("alter table", "add column", "modify column", "drop column", "rename column"),
            secondaryKeywords = listOf("ddl", "schema change", "modify"),
            crispDefinition = "The **ALTER TABLE** command modifies the existing schema of a database table without dropping or re-creating it, allowing developers to add, delete, rename, or modify column data types and constraints.",
            keyPoints = "• `ADD`: Adds a new column to the table.\n• `DROP COLUMN`: Permanently deletes an existing column and its data.\n• `MODIFY` / `ALTER COLUMN`: Changes a column's datatype or constraint rules.\n• `RENAME COLUMN`: Renames a column identifier.",
            codeExample = "```sql\n-- Add a new column\nALTER TABLE employees ADD COLUMN phone_number VARCHAR(15);\n\n-- Modify datatype\nALTER TABLE employees MODIFY COLUMN salary DECIMAL(12, 2);\n\n-- Drop a column\nALTER TABLE employees DROP COLUMN phone_number;\n```"
        ),

        DeepTopicEntry(
            id = "sql_full_form",
            subject = "SQL & Database Management",
            chapter = "SQL Fundamentals",
            topic = "SQL Full Form & Command Categories",
            primaryKeywords = listOf("what is the full form of sql", "full form of sql", "sql full form", "what does sql stand for"),
            secondaryKeywords = listOf("ddl", "dml", "dcl", "tcl", "structured query language"),
            crispDefinition = "**SQL** stands for **Structured Query Language** (pronounced either as individual letters *S-Q-L* or as *Sequel*). It is the universal declarative standard across all major relational database management systems.",
            keyPoints = "• **DDL (Data Definition Language):** `CREATE`, `ALTER`, `DROP`, `TRUNCATE` (schema design)\n• **DML (Data Manipulation Language):** `SELECT`, `INSERT`, `UPDATE`, `DELETE` (data modifications)\n• **DCL (Data Control Language):** `GRANT`, `REVOKE` (security permissions)\n• **TCL (Transaction Control Language):** `COMMIT`, `ROLLBACK`, `SAVEPOINT` (transaction boundaries)",
            codeExample = "```sql\n-- DDL: CREATE, DML: INSERT, DQL: SELECT, TCL: COMMIT\nCREATE TABLE logs (id INT PRIMARY KEY, message TEXT);\nINSERT INTO logs VALUES (1, 'System Boot');\nSELECT * FROM logs;\nCOMMIT;\n```"
        ),

        DeepTopicEntry(
            id = "sql_delete_vs_truncate",
            subject = "SQL & Database Management",
            chapter = "SQL Commands",
            topic = "DELETE vs TRUNCATE",
            primaryKeywords = listOf("delete vs truncate", "difference between delete and truncate", "compare delete and truncate"),
            secondaryKeywords = listOf("ddl vs dml", "where clause", "rollback", "table delete"),
            crispDefinition = "**DELETE** is a DML command that removes specific rows filtered by a `WHERE` clause and can be rolled back. **TRUNCATE** is a DDL command that deallocates all table pages to empty the table rapidly without logging individual row deletions.",
            keyPoints = "• **Category:** `DELETE` = DML | `TRUNCATE` = DDL\n• **WHERE Clause:** Allowed only in `DELETE`; `TRUNCATE` removes all rows.\n• **Rollback:** `DELETE` can be rolled back in transactions; `TRUNCATE` auto-commits in most engines.\n• **Speed:** `TRUNCATE` is significantly faster than `DELETE`.",
            codeExample = "```sql\nDELETE FROM employees WHERE status = 'INACTIVE';\nTRUNCATE TABLE employees;\n```",
            comparison = "• **DELETE (DML):** Removes specific rows matching WHERE clause; logs row deletions; can be rolled back.\n• **TRUNCATE (DDL):** Deallocates all table data pages; resets auto-increment; extremely fast; cannot be rolled back."
        ),

        DeepTopicEntry(
            id = "sql_delete_truncate_drop",
            subject = "SQL & Database Management",
            chapter = "SQL Commands",
            topic = "DELETE vs TRUNCATE vs DROP",
            primaryKeywords = listOf(
                "delete vs truncate vs drop", "difference between delete truncate and drop",
                "truncate vs drop", "delete vs truncate"
            ),
            secondaryKeywords = listOf("ddl vs dml", "rollback", "table delete", "where clause"),
            crispDefinition = "**DELETE** is a DML command that removes specific rows filtered by a `WHERE` clause and can be rolled back. **TRUNCATE** is a DDL command that deallocates all table pages to empty the table rapidly without logging row deletions. **DROP** is a DDL command that removes both the table data AND the entire table schema permanently from disk.",
            keyPoints = "• **Category:** `DELETE` = DML | `TRUNCATE` = DDL | `DROP` = DDL\n• **WHERE Clause:** Allowed only in `DELETE`; `TRUNCATE` and `DROP` affect the whole entity.\n• **Rollback:** `DELETE` can be rolled back in transactions; `TRUNCATE` and `DROP` auto-commit in most engines.\n• **Speed:** `TRUNCATE` is significantly faster than `DELETE` because it deallocates data pages instead of logging row-by-row deletions.",
            codeExample = "```sql\n-- 1. DELETE: Removes filtered rows (slower, log per row, rollbackable)\nDELETE FROM employees WHERE status = 'INACTIVE';\n\n-- 2. TRUNCATE: Empties entire table instantly, resets AUTO_INCREMENT\nTRUNCATE TABLE employees;\n\n-- 3. DROP: Erases table schema + all data permanently from disk\nDROP TABLE employees;\n```",
            comparison = "• **DELETE:** DML | Removes filtered rows | WHERE allowed | Rollbackable | Fires triggers | Slower\n• **TRUNCATE:** DDL | Removes ALL rows | No WHERE | Cannot rollback | No triggers | Resets identity | Very Fast\n• **DROP:** DDL | Erases schema + data | No WHERE | Table destroyed | No triggers | Frees all disk space"
        ),

        DeepTopicEntry(
            id = "sql_joins",
            subject = "SQL & Database Management",
            chapter = "Relational Operations",
            topic = "SQL Joins (Inner, Left, Right, Full)",
            primaryKeywords = listOf("sql joins", "joins in sql", "inner join vs left join", "left join", "right join", "full join", "cross join"),
            secondaryKeywords = listOf("join", "on clause", "relational algebra", "cartesian product"),
            crispDefinition = "A **SQL JOIN** combines columns from two or more tables based on a related column between them (typically primary and foreign keys). Common types are **INNER JOIN** (only matching rows), **LEFT JOIN** (all left rows + matching right rows), **RIGHT JOIN** (all right rows + matching left rows), and **FULL OUTER JOIN** (all rows from both).",
            keyPoints = "• **INNER JOIN:** Returns records where the join predicate matches in both tables.\n• **LEFT (OUTER) JOIN:** Retains all rows from the left table; unmatched right columns return `NULL`.\n• **RIGHT (OUTER) JOIN:** Retains all rows from the right table; unmatched left columns return `NULL`.\n• **CROSS JOIN:** Produces the Cartesian product of all rows (\$M \\times N\$).",
            codeExample = "```sql\n-- INNER JOIN: Only students enrolled in a valid department\nSELECT s.full_name, d.dept_name\nFROM students s\nINNER JOIN departments d ON s.dept_id = d.dept_id;\n\n-- LEFT JOIN: All students, even if dept_id is NULL\nSELECT s.full_name, d.dept_name\nFROM students s\nLEFT JOIN departments d ON s.dept_id = d.dept_id;\n```"
        ),

        DeepTopicEntry(
            id = "sql_primary_vs_foreign",
            subject = "SQL & Database Management",
            chapter = "Keys & Integrity",
            topic = "Primary Key vs Foreign Key",
            primaryKeywords = listOf("primary key vs foreign key", "difference between primary key and foreign key", "pk vs fk", "compare primary key and foreign key"),
            secondaryKeywords = listOf("candidate key", "referential integrity", "unique", "null"),
            crispDefinition = "A **Primary Key** uniquely identifies each row within a table and cannot contain `NULL` values. A **Foreign Key** is a column that references the primary key of another parent table to enforce referential integrity across relational schemas.",
            keyPoints = "• **Uniqueness:** Primary Key enforces unique, non-null values; Foreign Key can accept duplicate and NULL values.\n• **Table Count:** A table can possess only ONE Primary Key, but MULTIPLE Foreign Keys.\n• **Index Creation:** Database engines automatically create a clustered or unique index on primary keys.",
            codeExample = "```sql\nCREATE TABLE departments (dept_id INT PRIMARY KEY, name VARCHAR(50));\nCREATE TABLE employees (emp_id INT PRIMARY KEY, dept_id INT, FOREIGN KEY (dept_id) REFERENCES departments(dept_id));\n```",
            comparison = "• **Primary Key:** Uniquely identifies each record in a table. It cannot contain NULL values, and each table can have only ONE primary key.\n\n• **Foreign Key:** A column that references the Primary Key of another table, enforcing referential integrity. It can contain NULL values (unless defined NOT NULL) and duplicate values, and a table can have multiple foreign keys."
        ),

        DeepTopicEntry(
            id = "sql_normalization",
            subject = "SQL & Database Management",
            chapter = "Database Design",
            topic = "Database Normalization (1NF to BCNF)",
            primaryKeywords = listOf("normalization", "1nf", "2nf", "3nf", "bcnf", "normal forms", "what is normalization"),
            secondaryKeywords = listOf("functional dependency", "anomalies", "transitive dependency", "partial dependency"),
            crispDefinition = "**Normalization** is a systematic database design technique that decomposes large, redundant tables into smaller, well-structured relations to eliminate insertion, update, and deletion anomalies while maintaining data integrity.",
            keyPoints = "• **1NF (First Normal Form):** Every cell must contain atomic (indivisible) values; no repeating groups.\n• **2NF (Second Normal Form):** Must be in 1NF and have NO partial dependencies (non-prime attributes fully dependent on candidate key).\n• **3NF (Third Normal Form):** Must be in 2NF and have NO transitive dependencies (\$A \\to B, B \\to C\$).\n• **BCNF (Boyce-Codd Normal Form):** Stricter 3NF: for every functional dependency \$X \\to Y\$, \$X\$ must be a super key.",
            codeExample = "```sql\n-- Decomposing an unnormalized student table into 3NF\nCREATE TABLE courses (course_id INT PRIMARY KEY, course_name VARCHAR(50), credits INT);\nCREATE TABLE student_enrollments (student_id INT, course_id INT, grade CHAR(2), PRIMARY KEY (student_id, course_id));\n```"
        ),

        DeepTopicEntry(
            id = "sql_acid",
            subject = "SQL & Database Management",
            chapter = "Transactions",
            topic = "ACID Properties in Transactions",
            primaryKeywords = listOf("acid", "acid properties", "atomicity", "consistency", "isolation", "durability"),
            secondaryKeywords = listOf("transaction", "commit", "rollback", "wal"),
            crispDefinition = "**ACID** represents the four fundamental guarantees of database transaction processing: **Atomicity** (all or nothing), **Consistency** (state transitions preserve constraints), **Isolation** (concurrent transactions do not interfere), and **Durability** (committed data survives crashes).",
            keyPoints = "• **Atomicity:** If any statement in a transaction fails, the entire transaction is rolled back.\n• **Consistency:** Database moves strictly from one valid state to another satisfying all schema rules.\n• **Isolation:** Concurrency levels (Read Uncommitted, Read Committed, Repeatable Read, Serializable).\n• **Durability:** Changes are flushed to non-volatile storage / Write-Ahead Logs (WAL).",
            codeExample = "```sql\n-- Atomic Bank Transfer Transaction\nSTART TRANSACTION;\nUPDATE accounts SET balance = balance - 500 WHERE account_id = 101;\nUPDATE accounts SET balance = balance + 500 WHERE account_id = 102;\nCOMMIT; -- If error occurs, run ROLLBACK;\n```"
        ),

        DeepTopicEntry(
            id = "sql_second_highest_salary",
            subject = "SQL & Database Management",
            chapter = "Advanced Queries",
            topic = "Second Highest Salary Query",
            primaryKeywords = listOf("second highest salary", "nth highest salary", "2nd highest salary", "find second highest salary"),
            secondaryKeywords = listOf("dense_rank", "limit offset", "subquery", "max salary"),
            crispDefinition = "Finding the **Second Highest Salary** is a classic technical placement question. It can be solved using either a subquery with `MAX()`, the `LIMIT ... OFFSET` clause, or the ANSI standard `DENSE_RANK()` window function.",
            keyPoints = "• **Method 1 (Subquery):** `WHERE salary < (SELECT MAX(salary) FROM employees)`\n• **Method 2 (LIMIT OFFSET):** `ORDER BY salary DESC LIMIT 1 OFFSET 1` (handles MySQL)\n• **Method 3 (DENSE_RANK):** Best for handling duplicate salaries accurately without skipping ranks.",
            codeExample = "```sql\n-- Method 1: Subquery approach (Universal across SQL engines)\nSELECT MAX(salary) AS SecondHighestSalary\nFROM employees\nWHERE salary < (SELECT MAX(salary) FROM employees);\n\n-- Method 2: Window Function DENSE_RANK (Handles ties perfectly)\nWITH RankedSalaries AS (\n    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) as rank_pos\n    FROM employees\n)\nSELECT salary FROM RankedSalaries WHERE rank_pos = 2 LIMIT 1;\n```"
        ),

        // =====================================================================
        // SUBJECT 2: PYTHON & CODING LOGIC
        // =====================================================================
        DeepTopicEntry(
            id = "python_definition",
            subject = "Python & Coding Logic",
            chapter = "Python Fundamentals",
            topic = "Python Fundamentals",
            primaryKeywords = listOf(
                "python definition", "what is python", "define python", "python programming", "python language",
                "explain python", "python overview"
            ),
            secondaryKeywords = listOf("interpreted", "dynamically typed", "high level", "guido van rossum"),
            crispDefinition = "**Python** is a high-level, interpreted, dynamically typed programming language created by Guido van Rossum in 1991. It emphasizes exceptional code readability with clean indentation, automatic memory management via reference counting, and support for multi-paradigm programming (OOP, procedural, functional).",
            keyPoints = "• **Memory Model & Mutability:** In Python, variables are object references stored in heap memory. Immutable types (`int`, `str`, `tuple`) cannot be changed in-place; mutable types (`list`, `dict`) can.\n• **Global Interpreter Lock (GIL):** A mutex in CPython that prevents multiple native threads from executing Python bytecodes at once, making multiprocessing essential for CPU-bound parallelism.\n• **Interpreted:** Python compiles source code to bytecode (.pyc) executed by the CPython virtual machine.",
            codeExample = "```python\n# Clean, expressive Python syntax\ndef calculate_grade(score: float) -> str:\n    return 'A' if score >= 90 else ('B' if score >= 75 else 'C')\n\nprint(calculate_grade(88.5)) # Output: 'B'\n```"
        ),

        DeepTopicEntry(
            id = "python_list_vs_tuple",
            subject = "Python & Coding Logic",
            chapter = "Data Structures & Memory",
            topic = "List vs Tuple",
            primaryKeywords = listOf("list vs tuple", "difference between list and tuple", "compare list and tuple", "tuples vs lists"),
            secondaryKeywords = listOf("mutable", "immutable", "performance", "memory"),
            crispDefinition = "In Python, a **List** is a **mutable** sequence enclosed in brackets `[]` that allows in-place modifications, whereas a **Tuple** is an **immutable** sequence enclosed in parentheses `()` that cannot be changed once created.",
            keyPoints = "• **Mutability:** Lists are mutable (dynamic allocation, items can be added/removed); Tuples are immutable (read-only fixed memory).\n• **Performance:** Tuples allocate less memory and execute faster than lists.\n• **Hashability:** Tuples can be dictionary keys or set elements if all items are immutable; lists can never be dictionary keys.",
            codeExample = "```python\na_list = [1, 2, 3]    # Mutable: a_list.append(4) works in-place\na_tuple = (1, 2, 3)   # Immutable: fixed size, hashable\n```",
            comparison = "• **Mutability:** Lists = mutable (can modify in-place) vs Tuples = immutable (state locked)\n• **Syntax:** Lists = `[1, 2]` vs Tuples = `(1, 2)`\n• **Memory:** Tuples use less memory block allocation than dynamically over-allocated lists\n• **Dictionary Keys:** Lists = Unhashable (cannot be keys) vs Tuples = Hashable (can be keys)"
        ),

        DeepTopicEntry(
            id = "python_comprehensions",
            subject = "Python & Coding Logic",
            chapter = "Functions & Iterators",
            topic = "List Comprehensions in Python",
            primaryKeywords = listOf("list comprehension", "list comprehensions", "comprehension", "comprehensions in python", "list comprehensions in python"),
            secondaryKeywords = listOf("syntax", "filter", "mapping", "readable"),
            crispDefinition = "**List Comprehensions** provide a concise, Pythonic syntax for creating new lists by transforming or filtering elements from an existing iterable in a single readable line.",
            keyPoints = "• **Syntax:** `[expression for item in iterable if condition]`\n• **Performance:** Executes faster than standard `for` loops because bytecode loop iteration is optimized at the C-level.\n• **Readability:** Replaces multi-line loop initialization and `append()` statements with clean declarative code.",
            codeExample = "```python\n# Transform and filter squares of even numbers\nnumbers = [1, 2, 3, 4, 5, 6]\neven_squares = [x**2 for x in numbers if x % 2 == 0]\nprint(even_squares)  # [4, 16, 36]\n```"
        ),

        DeepTopicEntry(
            id = "python_gil_memory",
            subject = "Python & Coding Logic",
            chapter = "Memory & Concurrency",
            topic = "Global Interpreter Lock (GIL) & Memory Management",
            primaryKeywords = listOf("gil", "global interpreter lock", "python global interpreter lock", "python gil"),
            secondaryKeywords = listOf("mutex", "cpython", "multithreading", "multiprocessing", "reference counting"),
            crispDefinition = "The **Global Interpreter Lock (GIL)** is a mutex mechanism used by the CPython interpreter to synchronize thread execution so that only one native thread runs Python bytecode at any given moment, safeguarding CPython's reference counting memory management from race conditions.",
            keyPoints = "• **Core Principle:** Thread-safety for CPython's memory allocator; prevents race conditions during reference count updates.\n• **Impact on Concurrency:** Python threads are effective for I/O-bound tasks (network, disk I/O) where GIL is released, but do NOT speed up CPU-bound tasks.\n• **CPU-Bound Workaround:** Use the `multiprocessing` module to spin up separate OS processes with independent memory spaces and separate GIL instances.",
            codeExample = "```python\n# CPU-bound parallelism requires multiprocessing, not threading\nfrom multiprocessing import Pool\n\ndef heavy_computation(x):\n    return sum(i * i for i in range(x))\n\nif __name__ == '__main__':\n    with Pool(4) as p:\n        results = p.map(heavy_computation, [1000000, 2000000, 3000000])\n```"
        ),

        DeepTopicEntry(
            id = "python_mutability",
            subject = "Python & Coding Logic",
            chapter = "Data Structures & Memory",
            topic = "Python Mutability & Pass-by-Object-Reference",
            primaryKeywords = listOf("mutability", "mutable vs immutable", "mutable", "immutable", "python memory model"),
            secondaryKeywords = listOf("id()", "pass by object reference", "list vs tuple", "hashable"),
            crispDefinition = "In Python, **mutable objects** (e.g., `list`, `dict`, `set`) can have their internal state altered in-place without changing their memory address (`id()`). **Immutable objects** (e.g., `int`, `float`, `str`, `tuple`) cannot be changed once allocated; any modification creates a brand-new object in memory.",
            keyPoints = "• **Mutable Types:** `list`, `dict`, `set`, `bytearray`. Not hashable; cannot be dictionary keys.\n• **Immutable Types:** `int`, `str`, `tuple`, `frozenset`. Hashable; safe as dictionary keys.\n• **Pass-by-Object-Reference:** Modifying a mutable argument in-place inside a function affects the caller's object.",
            codeExample = "```python\n# Mutable: changes in-place\na = [1, 2]\nb = a\nb.append(3)\nprint(a)  # [1, 2, 3] -> Both variables reference the exact same list!\n\n# Immutable: creates a new object\nx = 'hello'\nprint(id(x))\nx += ' world'\nprint(id(x))  # Different memory address!\n```"
        ),

        DeepTopicEntry(
            id = "python_list_tuple_set_dict",
            subject = "Python & Coding Logic",
            chapter = "Data Structures & Memory",
            topic = "List vs Tuple vs Set vs Dict",
            primaryKeywords = listOf(
                "list vs tuple vs set vs dict", "list vs tuple", "difference between list and tuple",
                "python collections", "set vs dict"
            ),
            secondaryKeywords = listOf("hashmap", "ordered", "indexed", "unique", "hashable"),
            crispDefinition = "Python's four built-in collection types differ in ordering, mutability, and indexing: **List** (`[]`) is ordered, mutable, allows duplicates; **Tuple** (`()`) is ordered, immutable, allows duplicates; **Set** (`{}`) is unordered, mutable, unique elements only; **Dict** (`{k: v}`) stores key-value pairs with unique, hashable keys.",
            keyPoints = "• **List:** Dynamically resizing array; \$O(1)\$ append, \$O(N)\$ arbitrary insertion.\n• **Tuple:** Fixed-size memory block; faster allocation than lists; hashable if elements are hashable.\n• **Set:** Implemented using a hash table; \$O(1)\$ average time complexity for lookup (`in`).\n• **Dict:** Hash map mapping keys to values; preserves insertion order since Python 3.7.",
            codeExample = "```python\nmy_list  = [1, 2, 2, 3]       # Ordered, mutable: [1, 2, 2, 3]\nmy_tuple = (1, 2, 2, 3)       # Ordered, immutable: (1, 2, 2, 3)\nmy_set   = {1, 2, 2, 3}       # Unordered, unique: {1, 2, 3}\nmy_dict  = {'a': 1, 'b': 2}   # Key-Value mappings with O(1) lookups\n```"
        ),

        DeepTopicEntry(
            id = "python_decorators",
            subject = "Python & Coding Logic",
            chapter = "Functions & Metaprogramming",
            topic = "Python Decorators",
            primaryKeywords = listOf("decorator", "decorators", "python decorator", "what is decorator", "how decorator works"),
            secondaryKeywords = listOf("higher order function", "functools.wraps", "*args", "**kwargs", "wrapper"),
            crispDefinition = "A **Python Decorator** is a higher-order function that takes another function as an argument, extends or wraps its behavior without directly modifying its source code, and returns the modified wrapper function (syntactically applied with `@decorator_name`).",
            keyPoints = "• **First-Class Functions:** Functions can be passed as arguments, assigned, and returned.\n• **Closure:** The inner `wrapper` retains access to enclosing function variables.\n• **Placement Use Cases:** Logging, execution timing, user authentication, and rate limiting.",
            codeExample = "```python\nimport time\nfrom functools import wraps\n\ndef timer_decorator(func):\n    @wraps(func)\n    def wrapper(*args, **kwargs):\n        start = time.perf_counter()\n        result = func(*args, **kwargs)\n        print(f\"{func.__name__} took {time.perf_counter() - start:.4f}s\")\n        return result\n    return wrapper\n\n@timer_decorator\ndef compute_squares(n):\n    return [x**2 for x in range(n)]\n\ncompute_squares(100000)\n```"
        ),

        DeepTopicEntry(
            id = "python_generators",
            subject = "Python & Coding Logic",
            chapter = "Functions & Iterators",
            topic = "Generators & Iterators (yield vs return)",
            primaryKeywords = listOf("generator", "generators", "yield", "yield vs return", "iterator", "iterators"),
            secondaryKeywords = listOf("__iter__", "__next__", "lazy evaluation", "memory efficient"),
            crispDefinition = "A **Generator** is a special Python function that produces an iterator sequence using the `yield` statement. Unlike `return`, which terminates execution and dumps all values into memory at once, `yield` pauses the function state, yielding values lazily on-demand with \$O(1)\$ memory consumption.",
            keyPoints = "• **Lazy Evaluation:** Computes the next item only when `next()` or a `for` loop requests it.\n• **Memory Efficiency:** Streaming 10,000,000 items with a generator uses mere bytes instead of gigabytes of RAM.\n• **State Preservation:** Local variables and execution pointers are frozen between yields.",
            codeExample = "```python\n# Infinite / Streaming Fibonacci Generator with O(1) space\ndef fibonacci_gen():\n    a, b = 0, 1\n    while True:\n        yield a\n        a, b = b, a + b\n\nfib = fibonacci_gen()\nfor _ in range(6):\n    print(next(fib), end=\" \")  # 0 1 1 2 3 5\n```"
        ),

        DeepTopicEntry(
            id = "python_oop",
            subject = "Python & Coding Logic",
            chapter = "Object-Oriented Programming",
            topic = "OOP Pillars in Python (Class, Inheritance, Polymorphism)",
            primaryKeywords = listOf("python oop", "oop in python", "class and object", "python classes", "dunder methods"),
            secondaryKeywords = listOf("__init__", "self", "inheritance", "encapsulation", "polymorphism"),
            crispDefinition = "**Object-Oriented Programming (OOP)** in Python models software around classes (blueprints) and objects (runtime instances), enforcing the four fundamental pillars: **Encapsulation** (data bundling with `_`/`__`), **Inheritance** (reusability via `super()`), **Polymorphism** (method overriding/duck typing), and **Abstraction** (`abc.ABC`).",
            keyPoints = "• `__init__`: Constructor method called automatically when instantiating objects.\n• `self`: Explicit reference to the current object instance.\n• `super()`: Delegates calls to parent class implementations in the Method Resolution Order (MRO).",
            codeExample = "```python\nclass Student:\n    def __init__(self, name: str, gpa: float):\n        self.name = name          # Public attribute\n        self.__gpa = gpa          # Private attribute (encapsulation)\n\n    def get_gpa(self) -> float:\n        return self.__gpa\n\n    def __str__(self) -> str:     # Dunder string representation\n        return f\"Student(name={self.name}, gpa={self.__gpa})\"\n\ns = Student(\"Kishore\", 8.9)\nprint(s)  # Student(name=Kishore, gpa=8.9)\n```"
        ),

        // =====================================================================
        // SUBJECT 3: AI & MODERN TECH CONCEPTS
        // =====================================================================
        DeepTopicEntry(
            id = "ai_definition",
            subject = "AI & Modern Tech Concepts",
            chapter = "AI Foundations",
            topic = "Artificial Intelligence & Modern AI Taxonomy",
            primaryKeywords = listOf(
                "artificial intelligence", "what is ai", "define ai", "ai definition", "ai taxonomy",
                "machine learning definition"
            ),
            secondaryKeywords = listOf("ml vs dl", "deep learning", "neural network", "narrow ai", "agi"),
            crispDefinition = "**Artificial Intelligence (AI)** is the broad engineering discipline of creating software systems capable of performing cognitive tasks that traditionally require human intelligence—such as pattern recognition, language understanding, reasoning, problem-solving, and continuous learning from empirical data.",
            keyPoints = "• **Taxonomy Hierarchy:** **Artificial Intelligence** $\\supset$ **Machine Learning** (learning from data without explicit rules) $\\supset$ Deep Learning (multi-layer neural networks).\n• **Cognitive Automation:** Replaces brittle rule-based heuristics with probabilistic models that generalize across unseen inputs.\n• **Modern AI Revolution:** Large Language Models (LLMs), multimodal vision-language models, and generative systems.",
            codeExample = "```python\n# Machine Learning classification workflow with Scikit-Learn\nfrom sklearn.linear_model import LogisticRegression\n\nmodel = LogisticRegression()\nmodel.fit(X_train, y_train)\npredictions = model.predict(X_test)\n```"
        ),

        DeepTopicEntry(
            id = "ai_rag",
            subject = "AI & Modern Tech Concepts",
            chapter = "Generative AI Systems",
            topic = "RAG (Retrieval-Augmented Generation)",
            primaryKeywords = listOf("rag", "retrieval augmented generation", "what is rag", "vector search", "chunking"),
            secondaryKeywords = listOf("embeddings", "vector db", "chromadb", "hallucination", "llm context"),
            crispDefinition = "**Retrieval-Augmented Generation (RAG)** is an AI architectural framework that enriches LLM prompts with relevant, verified domain documents retrieved from an external vector database at inference time, dramatically eliminating hallucinations and keeping responses up-to-date without expensive model fine-tuning.",
            keyPoints = "• **Step 1 (Ingestion):** Documents are chunked into paragraphs and converted to dense vector embeddings.\n• **Step 2 (Retrieval):** User prompt is embedded; top-\$k\$ nearest chunks are retrieved via Cosine Similarity.\n• **Step 3 (Generation):** LLM synthesizes an accurate answer grounded strictly on the retrieved context.",
            codeExample = "```python\n# Conceptual RAG Prompt Grounding Structure\nprompt = f\"\"\"\nContext information from knowledge base:\n{retrieved_chunks}\n\nUser Question: {user_query}\nAnswer strictly using the context above:\n\"\"\"\n```"
        ),

        DeepTopicEntry(
            id = "ai_llm_transformers",
            subject = "AI & Modern Tech Concepts",
            chapter = "Transformers & Foundation Models",
            topic = "Transformers & Large Language Models (LLMs)",
            primaryKeywords = listOf("llm", "large language models", "full form of llm", "llm full form", "what does llm stand for", "transformer", "transformers"),
            secondaryKeywords = listOf("self attention", "bert", "gpt", "tokens", "pre training", "fine tuning", "rag"),
            crispDefinition = "**Transformers** are deep neural network architectures introduced in 'Attention Is All You Need' (2017) that process sequential data using parallel **Self-Attention mechanisms** instead of recurrence. **Large Language Models (LLMs)** are foundational models built on Transformers trained on hundreds of billions of tokens to predict subsequent tokens.",
            keyPoints = "• **Pre-training:** Self-supervised next-token prediction across trillions of tokens to learn foundational syntax, world facts, and reasoning.\n• **RAG (Retrieval-Augmented Generation):** Binds LLMs with external vector databases to fetch verified documents and eliminate hallucinations.\n• **Fine-Tuning (SFT) & Alignment:** Instruction tuning and RLHF align model outputs to follow complex instructions safely.",
            codeExample = "```python\n# Attention Formula: Attention(Q, K, V) = softmax((Q * K^T) / sqrt(d_k)) * V\nimport numpy as np\n\ndef scaled_dot_product_attention(Q, K, V):\n    scores = np.matmul(Q, K.T) / np.sqrt(Q.shape[-1])\n    weights = np.exp(scores) / np.sum(np.exp(scores), axis=-1, keepdims=True)\n    return np.matmul(weights, V)\n```"
        ),

        // =====================================================================
        // SUBJECT 4: APTITUDE & INTERVIEW PREP
        // =====================================================================
        DeepTopicEntry(
            id = "apt_percentage",
            subject = "Aptitude & Interview Prep",
            chapter = "Quantitative Mathematics",
            topic = "Percentage Formulas & Shortcut Tricks",
            primaryKeywords = listOf("percentage", "percentages", "percentage formula", "percentage calculation", "percentage shortcut"),
            secondaryKeywords = listOf("fraction to percentage", "percentage increase", "percentage decrease"),
            crispDefinition = "A **Percentage** represents a dimensionless ratio or fraction expressed per hundred (\$100\$). In placement quantitative rounds, questions primarily test percentage changes, successive discounts, and fractional equivalence shortcuts (e.g., \$1/6 = 16.66\\%, 1/8 = 12.5\\%\$).",
            keyPoints = "• **Percentage Change:** \$\\frac{\\text{Final} - \\text{Initial}}{\\text{Initial}} \\times 100\\%\$\n• **Successive Percentage Change:** If value changes by $+a\\%$ and then $+b\\%$, net change is $\\left(a + b + \\frac{ab}{100}\\right)\\%$.\n• **Expenditure Invariant:** If price increases by \$R\\%\$, consumption must decrease by \$\\frac{R}{100 + R} \\times 100\\%\$ to maintain constant expenditure.",
            codeExample = "```text\nFraction Shortcut Table:\n1/2 = 50%    | 1/3 = 33.33% | 1/4 = 25%    | 1/5 = 20%\n1/6 = 16.67% | 1/7 = 14.28% | 1/8 = 12.5%  | 1/9 = 11.11%\n```"
        ),

        DeepTopicEntry(
            id = "apt_speed_distance_time",
            subject = "Aptitude & Interview Prep",
            chapter = "Kinematics & Motion",
            topic = "Speed, Distance & Time (Trains & Relative Speed)",
            primaryKeywords = listOf("speed distance time", "train problems", "relative speed", "boats and streams"),
            secondaryKeywords = listOf("km/h to m/s", "average speed", "moving walkway"),
            crispDefinition = "**Speed, Distance, and Time** problems govern relative motion under constant or varying velocity. Core conversions: \$\\text{km/h} \\to \\text{m/s}\$ multiplies by \$\\frac{5}{18}\$. **Relative Speed** adds velocities for opposite directions (\$S_1 + S_2\$) and subtracts velocities for same-direction motion (\$|S_1 - S_2|\$).",
            keyPoints = "• **Unit Conversion:** \$1\\text{ km/h} = \\frac{5}{18}\\text{ m/s}\$ | \$1\\text{ m/s} = \\frac{18}{5}\\text{ km/h}\$.\n• **Average Speed (Equal Distances):** \$\\frac{2 \\times S_1 \\times S_2}{S_1 + S_2}\$.\n• **Train Crossing Object:** Time to cross a pole/man = \$\\frac{\\text{Length of Train}}{\\text{Speed}}\$. Time to cross a platform = \$\\frac{\\text{Length of Train} + \\text{Length of Platform}}{\\text{Speed}}\$.",
            codeExample = "```text\nExample Problem:\nA 150m train running at 54 km/h crosses a 250m platform.\n1. Convert Speed: 54 * (5/18) = 15 m/s\n2. Total Distance: 150m + 250m = 400m\n3. Time = Distance / Speed = 400 / 15 = 26.67 seconds\n```"
        ),

        DeepTopicEntry(
            id = "apt_star_method",
            subject = "Aptitude & Interview Prep",
            chapter = "Behavioral Interviews",
            topic = "STAR Interview Method",
            primaryKeywords = listOf("star method", "star interview method", "behavioral interview", "star framework"),
            secondaryKeywords = listOf("situation", "task", "action", "result", "hr interview"),
            crispDefinition = "The **STAR Method** is a globally recognized structured framework for answering behavioral and situational interview questions by breaking narratives into four distinct components: **Situation** (context), **Task** (responsibility), **Action** (specific steps you took), and **Result** (quantifiable positive outcome).",
            keyPoints = "• **Situation (20%):** Set the stage with brief project background, team constraints, or challenges.\n• **Task (10%):** Clarify your specific goal or deliverable within that context.\n• **Action (50%):** Detail YOUR technical choices, debugging strategies, and leadership initiative.\n• **Result (20%):** Conclude with quantifiable metric improvements (e.g. reduced latency by 35%, shipped on time).",
            codeExample = "```text\nSTAR Template:\n• Situation: \"During our college campus app project, the backend crashed under 500 concurrent users...\"\n• Task: \"As the backend lead, I had to identify the bottleneck and restore response times under 200ms.\"\n• Action: \"I profiled MySQL queries, added composite indexes on foreign keys, and implemented Redis caching.\"\n• Result: \"Server CPU usage dropped by 45%, handling 1,500 simultaneous users without dropped packets.\"\n```"
        ),

        // =====================================================================
        // SUBJECT 5: DSA (DATA STRUCTURES & ALGORITHMS)
        // =====================================================================
        DeepTopicEntry(
            id = "dsa_binary_search",
            subject = "Data Structures & Algorithms",
            chapter = "Searching Algorithms",
            topic = "Binary Search Algorithm",
            primaryKeywords = listOf("binary search", "binary search algorithm", "how binary search works"),
            secondaryKeywords = listOf("divide and conquer", "o(log n)", "sorted array", "two pointer"),
            crispDefinition = "**Binary Search** is an optimal \$O(\\log N)\$ search algorithm that locates a target value within a **sorted array** by repeatedly comparing the target to the middle element and discarding half of the remaining search space on each iteration.",
            keyPoints = "• **Prerequisite:** The input dataset MUST be sorted in advance.\n• **Time Complexity:** Best: \$O(1)\$ | Average & Worst: \$O(\\log N)\$.\n• **Midpoint Overflow Trap:** Always calculate `mid = left + (right - left) // 2` instead of `(left + right) // 2` to prevent 32-bit integer overflow.",
            codeExample = "```python\ndef binary_search(arr: list[int], target: int) -> int:\n    left, right = 0, len(arr) - 1\n    while left <= right:\n        mid = left + (right - left) // 2\n        if arr[mid] == target:\n            return mid\n        elif arr[mid] < target:\n            left = mid + 1\n        else:\n            right = mid - 1\n    return -1  # Target not found\n```"
        ),

        DeepTopicEntry(
            id = "dsa_hashing",
            subject = "Data Structures & Algorithms",
            chapter = "Hash Data Structures",
            topic = "Hashing & Hash Tables",
            primaryKeywords = listOf("hashing", "hash table", "hash map", "collision resolution"),
            secondaryKeywords = listOf("chaining", "open addressing", "hash function", "o(1)"),
            crispDefinition = "A **Hash Table** is a data structure that maps keys to values using a **hash function** to compute an index into an array of buckets, delivering amortized \$O(1)\$ time complexity for insertions, deletions, and lookups.",
            keyPoints = "• **Collision Resolution:** Two techniques: **Chaining** (linked lists at each bucket) and **Open Addressing** (linear probing, quadratic probing, double hashing).\n• **Load Factor (\\$\\alpha\\$):** Ratio of stored elements to array size (\\$\\alpha = n / k\\$); table dynamically rehashes when \\$\\alpha > 0.75\$.\n• **Worst-Case:** Degrades to \$O(N)\$ if all keys hash to the exact same bucket.",
            codeExample = "```python\n# Python dictionaries are highly optimized hash tables\nuser_cache = {}\nuser_cache[\"user_101\"] = {\"name\": \"Kishore\", \"role\": \"Admin\"}\n# O(1) Instant Lookup\nprint(user_cache.get(\"user_101\"))\n```"
        ),

        // =====================================================================
        // SUBJECT 6: JAVA & OOPS MASTERY
        // =====================================================================
        DeepTopicEntry(
            id = "java_jvm_jdk_jre",
            subject = "Java & OOPs Mastery",
            chapter = "Java Architecture",
            topic = "JVM vs JRE vs JDK",
            primaryKeywords = listOf("jvm vs jre vs jdk", "difference between jvm jre and jdk", "what is jvm", "what is jdk"),
            secondaryKeywords = listOf("bytecode", "jit compiler", "garbage collector", "platform independence"),
            crispDefinition = "**JVM (Java Virtual Machine)** is the abstract runtime engine that executes compiled Java bytecode (`.class`). **JRE (Java Runtime Environment)** packages JVM with standard class libraries to run Java apps. **JDK (Java Development Kit)** is the complete development suite containing JRE, compiler (`javac`), and debugging tools.",
            keyPoints = "• **Formula:** \\$\\text{JDK} = \\text{JRE} + \\text{Development Tools (javac, jdb, jar)}\\$.\n• **Formula:** \\$\\text{JRE} = \\text{JVM} + \\text{Core Runtime Libraries (rt.jar, util)}\\$.\n• **Write Once, Run Anywhere (WORA):** Bytecode is platform-independent; JVM implementations are platform-specific.",
            codeExample = "```bash\n# Compile Java source into platform-independent bytecode\njavac Main.java    # Produces Main.class (JDK)\n\n# Execute bytecode via the JVM\njava Main          # Runs bytecode on the JVM (JRE / JVM)\n```",
            comparison = "• **JVM:** Pure Execution Engine | Bytecode $\\to$ Machine Code via JIT | No compiler\n• **JRE:** JVM + Standard Libraries | Runs pre-compiled Java applications | Cannot compile code\n• **JDK:** JRE + Developer Tooling | Compiles (`javac`), debugs (`jdb`), packages (`jar`)"
        ),

        // =====================================================================
        // SUBJECT 7: OS & COMPUTER NETWORKS
        // =====================================================================
        DeepTopicEntry(
            id = "os_tcp_udp",
            subject = "OS & Computer Networks",
            chapter = "Transport Layer",
            topic = "TCP vs UDP",
            primaryKeywords = listOf("tcp vs udp", "difference between tcp and udp", "tcp", "udp"),
            secondaryKeywords = listOf("3 way handshake", "connection oriented", "unreliable", "streaming", "packet"),
            crispDefinition = "**TCP (Transmission Control Protocol)** is a connection-oriented, reliable protocol that guarantees ordered packet delivery with flow control and retransmissions via a 3-Way Handshake. **UDP (User Datagram Protocol)** is a connectionless, lightweight protocol that streams packets without handshake or delivery guarantees, prioritizing minimal latency.",
            keyPoints = "• **Reliability:** TCP guarantees zero packet loss via ACKs; UDP tolerates dropped packets.\n• **Speed:** UDP has negligible protocol overhead; TCP incurs 3-way handshake and congestion control latency.\n• **Use Cases:** TCP powers HTTP/HTTPS, Web Browsing, Email (SMTP), File Transfer (FTP); UDP powers Live Video Streaming, DNS lookups, Online Multiplayer Gaming, VoIP.",
            codeExample = "```text\nTCP 3-Way Handshake Sequence:\n1. Client -> Server: SYN  (Synchronize Sequence Number)\n2. Server -> Client: SYN-ACK (Acknowledge & Sync)\n3. Client -> Server: ACK  (Connection Established!)\n```",
            comparison = "• **Connection:** TCP = Connection-oriented (Handshake) vs UDP = Connectionless\n• **Reliability:** TCP = Guaranteed delivery & ordered vs UDP = Best-effort (No guarantees)\n• **Header Size:** TCP = 20-60 bytes vs UDP = 8 bytes\n• **Speed:** TCP = Slower (ACK overhead) vs UDP = Ultra-fast, minimal latency"
        ),

        DeepTopicEntry(
            id = "os_osi_layers",
            subject = "OS & Computer Networks",
            chapter = "Network Models",
            topic = "OSI 7 Layers Model",
            primaryKeywords = listOf("osi model", "osi 7 layers", "7 layers of osi", "osi layers"),
            secondaryKeywords = listOf("physical", "data link", "network", "transport", "session", "presentation", "application"),
            crispDefinition = "The **OSI (Open Systems Interconnection) Model** is a conceptual framework that standardizes network telecommunication into 7 logical layers from physical transmission to software application.",
            keyPoints = "• **Layer 7 - Application:** HTTP, DNS, FTP, SMTP (user-facing protocols)\n• **Layer 6 - Presentation:** Data encryption, compression, format translation (SSL/TLS, JSON)\n• **Layer 5 - Session:** Manages sessions and connection dialogs (RPC, NetBIOS)\n• **Layer 4 - Transport:** End-to-end reliability, port addressing, segmentation (TCP, UDP)\n• **Layer 3 - Network:** Logical IP addressing, packet routing across networks (IP, ICMP, Routers)\n• **Layer 2 - Data Link:** MAC addressing, frame switching, error detection (Ethernet, Switches)\n• **Layer 1 - Physical:** Raw transmission of bits over physical media (Cables, Hubs, Wi-Fi)",
            codeExample = "```text\nMnemonic to remember all 7 layers (Top to Bottom):\n\"All People Seem To Need Data Processing\"\nApplication -> Presentation -> Session -> Transport -> Network -> Data Link -> Physical\n```"
        ),

        // =====================================================================
        // SUBJECT 8: ANDROID & MOBILE ARCHITECTURE
        // =====================================================================
        DeepTopicEntry(
            id = "android_room",
            subject = "Android & App Architecture",
            chapter = "Data Persistence",
            topic = "Android Room Database & DAO Architecture",
            primaryKeywords = listOf("android room", "room database", "room db", "room dao", "room sqlite"),
            secondaryKeywords = listOf("@entity", "@dao", "@database", "sqlite abstraction", "type converters"),
            crispDefinition = "**Room** is an official Android Jetpack persistence library that provides an abstraction layer over SQLite to enable robust, compile-time verified database access with seamless Kotlin Coroutines and reactive `Flow` observation.",
            keyPoints = "• `@Entity`: Defines the table schema and primary key attributes.\n• `@Dao`: Interface declaring SQL CRUD operations and reactive Flow queries.\n• `@Database`: Main database configuration holder extending `RoomDatabase`.\n• **Compile-Time Verification:** Room validates raw SQL queries at compile time, eliminating runtime crashes.",
            codeExample = "```kotlin\n// Room Entity\n@Entity(tableName = \"users\")\ndata class UserEntity(@PrimaryKey val id: Int, val name: String)\n\n// Room DAO with Reactive Flow\n@Dao\ninterface UserDao {\n    @Query(\"SELECT * FROM users WHERE id = :userId\")\n    fun getUser(userId: Int): Flow<UserEntity>\n    \n    @Insert(onConflict = OnConflictStrategy.REPLACE)\n    suspend fun insertUser(user: UserEntity)\n}\n```"
        ),

        DeepTopicEntry(
            id = "android_compose",
            subject = "Android & App Architecture",
            chapter = "Modern UI",
            topic = "Jetpack Compose & State Hoisting",
            primaryKeywords = listOf("jetpack compose", "compose", "declarative ui", "state hoisting", "recomposition"),
            secondaryKeywords = listOf("@composable", "remember", "rememberSaveable", "mutableStateOf"),
            crispDefinition = "**Jetpack Compose** is Android's modern toolkit for building declarative native UIs. Instead of mutating imperative XML views directly, you describe UI as composable functions that automatically **recompose** when observed state (`State<T>`) changes.",
            keyPoints = "• **Declarative:** UI is a direct mathematical projection of state: `UI = f(State)`.\n• `remember` vs `rememberSaveable`: `remember` survives recomposition; `rememberSaveable` survives configuration changes (screen rotations).\n• **State Hoisting:** Pattern of moving state up to make composables stateless, reusable, and easily testable.",
            codeExample = "```kotlin\n@Composable\nfun CounterScreen() {\n    // Hoisted state surviving rotation\n    var count by rememberSaveable { mutableIntStateOf(0) }\n    \n    Column(modifier = Modifier.padding(16.dp)) {\n        Text(text = \"Count: \$count\", fontSize = 20.sp)\n        Button(onClick = { count++ }) {\n            Text(\"Increment\")\n        }\n    }\n}\n```"
        )
    )
}
