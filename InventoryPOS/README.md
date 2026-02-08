🛒 Enterprise Inventory & POS System
A robust, enterprise-grade Inventory Management and Point of Sale (POS) solution built with Java Swing and MySQL. This system is engineered for high-performance retail environments, featuring advanced database indexing and memory-optimized window management.

🚀 Key Features
Smart POS Terminal: Integrated checkout system with real-time stock validation and automated subtotal calculations.

Performance Optimization: Utilizes B-Tree Indexing on SQL tables for 10x faster search and reporting.

Executive Reporting: Comprehensive sales summaries with date-based filtering to track revenue and transaction history.

Low Stock Alerts: Proactive notification system for items falling below a 10-unit threshold.

Receipt Printing: Built-in support for generating and printing thermal-style receipts via Java's Printer API.

Inventory Paging: Efficiently handles thousands of records using server-side pagination (50 items per view).

Role-Based Security: Secured access for Admin and Cashier roles with encrypted-style password handling.

🛠️ Technical Stack
Language: Java (JDK 25)

Database: MySQL 8.0

Architecture: Data Access Object (DAO) Design Pattern

GUI Framework: Java Swing

Database Optimization: SQL Indexing & Connection Pooling

📂 Project Structure
Plaintext
src/
├── app/          # Main entry point and session management
├── dao/          # Data Access Objects (SQL Logic)
├── db/           # Database Connection & Auto-Setup
├── model/        # Plain Old Java Objects (POJOs)
└── ui/           # GUI Components (Swing Dialogs & Frames)



⚙️ Installation & Setup
Clone the Repository:

Bash
git clone https://github.com/YourUsername/InventoryPOS.git
Configure Database: Update db_config.properties in the root folder with your MySQL credentials.

Properties
db.user=your_username
db.pass=your_password
Run the Application: Execute Main.java. The system will automatically create the inventory_db database, tables, and performance indexes.

Default Credentials:

Username: admin

Password: admin123

📈 Optimization Details
To prevent system lag as data grows, this project implements:

SQL Indexing: Custom indexes on product_name and sale_date.

Memory Management: Automatic disposal of inactive modal windows using a single-active-dialog logic.

Relational Integrity: Enforced Foreign Key constraints to ensure data consistency.

👤 Author
Muhammad Shafay Siddiqui

Computer Science Student (Semester 4)
IBA Sukkur University
