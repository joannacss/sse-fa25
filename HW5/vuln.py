import sqlite3

# In-memory SQLite database setup for demonstration purposes
connection = sqlite3.connect(":memory:")
cursor = connection.cursor()

# Set up a simple users table
cursor.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT, password TEXT)")
cursor.execute("INSERT INTO users (username, password) VALUES ('admin', 'admin_pass')")
cursor.execute("INSERT INTO users (username, password) VALUES ('user', 'user_pass')")
connection.commit()


def vulnerable_function(query:str, x: int, y: int) -> None:
    """
    Vulnerable function with an SQL Injection vulnerability.
    The goal is to bypass authentication using malicious inputs.
    """
    if x + y > 100:
        if x - y < 10:
            cursor.execute(query)
            return cursor.fetchone()



# Example usage of the vulnerable function
if __name__ == "__main__":
    print(vulnerable_function("SELECT * FROM users WHERE username = 'admin' AND password = 'admin_pass'", 100, 95))  # Expected: Login successful, user returned
    print(vulnerable_function("SELECT * FROM users WHERE username = 'user' AND password = 'wrong_pass'", 100, 95))  # Expected: Login failed, None returned
