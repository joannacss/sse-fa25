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
    pass


def list_posts(request):
    pass


def view_post(request, post_id):
    pass


@require_POST
def logout(request):
    pass


def create_post(request):
    pass
