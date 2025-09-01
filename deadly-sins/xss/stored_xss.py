# An example of stored XSS
# To run: flask --app stored_xss  --debug run
# (The --debug will allow auto reload)
from flask import Flask, request, jsonify, render_template, redirect
from flask_sqlalchemy import SQLAlchemy


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///comments.db'
db = SQLAlchemy(app)

class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String(500))

    def __str__(self):
        return self.text

@app.route('/add_comment', methods=['POST'])
def add_comment():
    comment_data = request.form.get("comment")
    new_comment = Comment(text=comment_data)
    db.session.add(new_comment)
    db.session.commit()
    return redirect("/")



@app.route("/")
def hello_world():
    comments =  Comment.query.all()
    return render_template("blog.jinja", comments = comments)



@app.route('/delete_comments', methods=['DELETE'])
def delete_comments():
    num_deleted = db.session.query(Comment).delete()
    db.session.commit()
    return jsonify({"message": f"Deleted {num_deleted} comments."})
# So we can use `curl -X DELETE http://127.0.0.1:5000/delete_comments` to delete comments and reset the database


if __name__ == "__main__":
    with app.app_context():
    	db.create_all()
    	app.run(debug=True)
