# An example of reflected XSS
# To run: flask --app live_reflected_xss  --debug run
# (The --debug will allow auto reload)

from flask import Flask
from flask import render_template, make_response, request
from html import escape


app = Flask(__name__)

@app.route("/")
def hello_world():
    # TODO: change hello world example to echo back the user's name 
    # passed to it as a request parameter
    n = request.args.get("name")
    # n = escape(n)
    return render_template("live_reflected_xss.jinja",name=n)





