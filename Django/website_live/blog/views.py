from django.shortcuts import render
from django.shortcuts import render, redirect
from django.urls import reverse
from .forms import RegisterForm

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
