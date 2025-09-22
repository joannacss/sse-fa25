from django.contrib.auth.hashers import check_password
import re

from django import forms
from django.contrib.auth.hashers import check_password
from django.contrib.auth.hashers import make_password
from django.core.exceptions import ValidationError

from .models import Post
from .models import User


class RegisterForm(forms.ModelForm):
    password1 = forms.CharField(label="Password", widget=forms.PasswordInput)
    password2 = forms.CharField(label="Confirm password", widget=forms.PasswordInput)

    class Meta:
        model = User
        fields = ("username", "email")  # password handled separately

    # TODO: Field-level validation (runs before clean())
    def clean_username(self):
        username = (self.cleaned_data.get("username") or "").strip()
        if len(username) < 4:
            raise ValidationError("Username has to be at least 4 characters long")
        if len(username) > 150:
            raise ValidationError("Username cannot be longer than 150 characters")
        if not re.fullmatch(r"\w+", username):
            raise ValidationError("Username can only contain letters, numbers, and underscores")
        return username

    # Field-level validation (runs before clean())
    def clean_password1(self):
        common = set()
        with open("passwords.txt", "r") as f:
            for line in f:
                common.add(line.strip())

        password = self.cleaned_data.get("password1")
        if len(password) < 8:
            raise ValidationError("Password must be at least 8 characters long")
        if len(password) > 128:
            raise ValidationError("Password cannot be longer than 128 characters")
        if not any(c.isdigit() for c in password):
            raise ValidationError("Password must contain at least one number")
        if not any(c.isalpha() for c in password):
            raise ValidationError("Password must contain at least one letter")
        if not any(c.isupper() for c in password):
            raise ValidationError("Password must contain at least one uppercase letter")
        if not any(c.islower() for c in password):
            raise ValidationError("Password must contain at least one lowercase letter")
        if not any(c in "!@#$%&" for c in password):
            raise ValidationError("Password must contain at least one special character (one of the following: !@#$%&")
        if password in common:
            raise ValidationError("Password is too common!")
        return password

    # Cross-field validation
    def clean(self):
        cleaned = super().clean()
        # TODO: enforce password matches
        p1 = self.cleaned_data.get("password1")
        p2 = self.cleaned_data.get("password2")
        if p1 and p2 and p1 != p2:
            raise ValidationError("Passwords do not match")
        return cleaned

    def save(self, commit=True):
        user = super().save(commit=False)
        # TODO: hash password and store it in user.password
        hashed_password = make_password(self.cleaned_data["password1"])
        user.password = hashed_password
        user.save() # INSERT (...) INTO user
        return user


class LoginForm(forms.Form):
    username = forms.CharField(max_length=200)
    password = forms.CharField(widget=forms.PasswordInput)

    # TODO: validation
    def clean(self):
        cleaned = super().clean()
        username = self.cleaned_data.get("username")
        password = self.cleaned_data.get("password")
        user = User.objects.get(username=username)

        if user and check_password(password, user.password):
            return cleaned
        raise ValidationError("Invalid username or password")

class PostForm(forms.ModelForm):
    class Meta:
        model = Post
        fields = ("title", "content")
