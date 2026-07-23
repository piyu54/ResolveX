CREATE DATABASE IF NOT EXISTS resolvex;

USE resolvex;

/* ===========================
   ROLES
=========================== */

CREATE TABLE roles (

    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    role_name VARCHAR(50) NOT NULL UNIQUE

);

INSERT INTO roles(role_name)
VALUES
('ADMIN'),
('MANAGER'),
('SUPPORT'),
('USER');


/* ===========================
   DEPARTMENTS
=========================== */

CREATE TABLE departments (

    department_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    department_name VARCHAR(100) NOT NULL UNIQUE

);

INSERT INTO departments(department_name)
VALUES
('IT'),
('HR'),
('Finance'),
('Administration'),
('Network');


/* ===========================
   CATEGORIES
=========================== */

CREATE TABLE categories (

    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    category_name VARCHAR(100) NOT NULL UNIQUE

);

INSERT INTO categories(category_name)
VALUES
('Hardware'),
('Software'),
('Network'),
('Printer'),
('Electricity');


/* ===========================
   PRIORITIES
=========================== */

CREATE TABLE priorities (

    priority_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    priority_name VARCHAR(50) NOT NULL UNIQUE

);

INSERT INTO priorities(priority_name)
VALUES
('LOW'),
('MEDIUM'),
('HIGH'),
('CRITICAL');

/* ===========================================
   USERS
=========================================== */

CREATE TABLE users (

    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    first_name VARCHAR(50) NOT NULL,

    last_name VARCHAR(50) NOT NULL,

    email VARCHAR(100) UNIQUE NOT NULL,

    password VARCHAR(255) NOT NULL,

    phone VARCHAR(15) UNIQUE,

    employee_code VARCHAR(20) UNIQUE,

    profile_image VARCHAR(255),

    status ENUM('ACTIVE','INACTIVE','BLOCKED')
    DEFAULT 'ACTIVE',

    role_id BIGINT NOT NULL,

    department_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY(role_id)
        REFERENCES roles(role_id),

    FOREIGN KEY(department_id)
        REFERENCES departments(department_id)

);


/* ===========================================
   COMPLAINT STATUS
=========================================== */

CREATE TABLE complaint_status (

    status_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    status_name VARCHAR(50) UNIQUE NOT NULL

);

INSERT INTO complaint_status(status_name)
VALUES
('OPEN'),
('IN_PROGRESS'),
('RESOLVED'),
('CLOSED');

/* ===========================================
   COMPLAINTS
=========================================== */

CREATE TABLE complaints (

    complaint_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    title VARCHAR(150) NOT NULL,

    description TEXT NOT NULL,

    created_by BIGINT NOT NULL,

    category_id BIGINT NOT NULL,

    priority_id BIGINT NOT NULL,

    status_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by)
        REFERENCES users(user_id),

    FOREIGN KEY (category_id)
        REFERENCES categories(category_id),

    FOREIGN KEY (priority_id)
        REFERENCES priorities(priority_id),

    FOREIGN KEY (status_id)
        REFERENCES complaint_status(status_id)

);

/* ===========================================
   COMMENTS
=========================================== */

CREATE TABLE comments (

    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    complaint_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,

    comment TEXT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (complaint_id)
        REFERENCES complaints(complaint_id),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)

);

/* ===========================================
   ATTACHMENTS
=========================================== */

CREATE TABLE attachments (

    attachment_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    complaint_id BIGINT NOT NULL,

    uploaded_by BIGINT NOT NULL,

    file_name VARCHAR(255) NOT NULL,

    file_path VARCHAR(500) NOT NULL,

    file_type VARCHAR(50) NOT NULL,

    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (complaint_id)
        REFERENCES complaints(complaint_id),

    FOREIGN KEY (uploaded_by)
        REFERENCES users(user_id)

);

/* ===========================================
   FEEDBACK
=========================================== */

CREATE TABLE feedback (

    feedback_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    complaint_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,

    rating INT NOT NULL,

    feedback TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (complaint_id)
        REFERENCES complaints(complaint_id),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)

);

/* ===========================================
   ASSIGNMENTS
=========================================== */

CREATE TABLE assignments (

    assignment_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    complaint_id BIGINT NOT NULL,

    assigned_to BIGINT NOT NULL,

    assigned_by BIGINT NOT NULL,

    remarks TEXT,

    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (complaint_id)
        REFERENCES complaints(complaint_id),

    FOREIGN KEY (assigned_to)
        REFERENCES users(user_id),

    FOREIGN KEY (assigned_by)
        REFERENCES users(user_id)

);

/* ===========================================
   NOTIFICATIONS
=========================================== */

CREATE TABLE notifications (

    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    complaint_id BIGINT,

    message VARCHAR(500) NOT NULL,

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    FOREIGN KEY (complaint_id)
        REFERENCES complaints(complaint_id)

);