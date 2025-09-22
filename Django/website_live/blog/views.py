from django.shortcuts import render, redirect, get_object_or_404
from django.urls import reverse
from django.views.decorators.http import require_POST

from .forms import RegisterForm, LoginForm, PostForm
from .models import Post, User


def index(request):
    return render(request, "blog/index.html")


# Create your views here.
def register(request):
    if request.method == "POST":
        # user submitted information
        form = RegisterForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect(reverse("blog:index"))
    else:
        form = RegisterForm()
    context = {"form": form}
    return render(request, "blog/register.html", context)


def login(request):
    if request.method == "POST":
        form = LoginForm(request.POST)
        if form.is_valid():
            request.session.cycle_key()
            request.session["user"] = form.cleaned_data["username"]
            return redirect(reverse("blog:index"))
    else:
        form = LoginForm
    context = {"form": form}
    return render(request, "blog/login.html", context)

def list_posts(request):
    all = Post.objects.all()
    context = {"posts": all}
    return render(request,
                  "blog/list.html",
                  context)



def view_post(request, post_id):
    # TODO: change it to handle non-existent blog posts
    post = Post.objects.get(id=post_id)
    return render(request, "blog/view.html", {"post": post})



@require_POST
def logout(request):
    request.session.flush()
    return redirect(reverse("blog:login"))


def create_post(request):
    if "user" not in request.session:
        return redirect(reverse("blog:login"))
    # user is logged in
    if request.method == "POST":
        form = PostForm(request.POST)
        if form.is_valid():
            post = form.save(commit=False)
            # user object?
            username = request.session["user"]
            user = User.objects.get(username=username)
            post.user = user
            post.save()
            return redirect(reverse("blog:index"))
    else:
        form = PostForm()
    context = {"form": form}
    return render(request, "blog/create.html", context)



