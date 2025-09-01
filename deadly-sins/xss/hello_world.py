# An example of a simple web application
# To run: 
#           flask --app hello_world  --debug run
# (The --debug will allow auto reload)

from flask import Flask
from flask import render_template, make_response



app = Flask(__name__)

@app.route("/")
def home():
    return """<!DOCTYPE html>
                <html>
                    <head>
                        <title>Basic example</title>
                    </head>
                    <body>
                        Homepage!
                    </body>
                </html>
    """


@app.route("/hello")
def hello_world():
    return render_template("hello.html", name="SSE Fall 2025!")

