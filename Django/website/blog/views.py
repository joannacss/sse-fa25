from django.shortcuts import render, redirect, get_object_or_404
from django.urls import reverse
from django.views.decorators.http import require_POST

from .forms import RegisterForm, LoginForm, PostForm
from .models import Post, User


def index(request):
    if request.session.get("user", None):
        return redirect(reverse("blog:list_posts"))
    return redirect(reverse("blog:login"))


# Create your views here.
def register(request):
    if request.method == "POST":
        form = RegisterForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect(reverse("blog:login"))
    else:
        form = RegisterForm()

    return render(request, "blog/register.html", {"form": form})


def login(request):
    if request.method == "POST":
        form = LoginForm(request.POST)
        if form.is_valid():
            request.session.cycle_key()  # Prevent session fixation
            request.session["user"] = form.user.username  # Store only a minimal identifier in session
            return redirect(reverse("blog:list_posts"))
    else:
        form = LoginForm()

    return render(request, "blog/login.html", {"form": form})


def list_posts(request):
    context = {
        "posts": Post.objects.all()
    }
    return render(request, 'blog/list.html', context)


def view_post(request, post_id):
    post = get_object_or_404(Post, pk=post_id)
    return render(request, 'blog/view.html', {'post': post})


@require_POST
def logout(request):
    # Wipe the session and rotate the session key
    request.session.flush()
    return redirect(reverse("blog:login"))


def create_post(request):
    user = request.session.get("user", None)
    if not user:
        return redirect(f"{reverse('blog:login')}")

    if request.method == "POST":
        form = PostForm(request.POST)
        if form.is_valid():
            post = form.save(commit=False)
            user = User.objects.filter(username=request.session["user"])[0]
            print(user)
            post.user = user
            post.save()
            return redirect(reverse("blog:list_posts"))
    else:
        form = PostForm()

    return render(request, "blog/create.html", {"form": form})
