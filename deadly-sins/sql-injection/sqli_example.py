"""
An example of a web application vulnerable with XSS
To run: flask --app sqli_example  --debug run
(The --debug will allow auto reload)
"""
from flask import Flask, request, render_template, render_template_string
from flask import redirect
import sqlite3

app = Flask(__name__)
DB_FILE = 'sqli_example.db'

# Initialize SQLite database
def init_db():
    print("Initializing DB")
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    cursor.execute('CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT, password TEXT, fullname TEXT)')
    
    # Insert some dummy data (but only if not exists)
    users = [
        ('admin', 'password123', 'J. Doe'),
        ('guest', 'guestpass', 'T. Smith')
    ]

    for username, password, fullname in users:
        # Check if the user already exists
        cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
        user = cursor.fetchone()

        # If user does not exist, insert it
        if not user:
            cursor.execute("INSERT INTO users (username, password, fullname) VALUES (?, ?, ?)", (username, password, fullname))

    conn.commit()
    conn.close()


# Homepage
@app.route('/')
def home():
    return render_template("home.html")
    

# Process the search input
@app.route('/search')
def search():
    username = request.args.get('username')

    # Vulnerable SQL Query (SQL Injection possible)
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    # query = f"SELECT * FROM users WHERE username = ?"  # SQL Injection vulnerability
    # cursor.execute(query, (username, ))

    query = f"SELECT * FROM users WHERE username = '{username}'" 
    cursor.executescript(query)
    
    results = cursor.fetchall()
    conn.close()

    # Display results
    return render_template("search.html", results=results)

# To mitigate change the query code above to:
# query = "SELECT * FROM users WHERE username = ?"
# cursor.execute(query, (username,))



if __name__ == "__main__":
    with app.app_context():
        init_db()
        app.run(debug=True)


    
    
