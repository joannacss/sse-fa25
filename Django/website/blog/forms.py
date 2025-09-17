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

    # Field-level validation (runs before clean())
    def clean_username(self):
        username = (self.cleaned_data.get("username") or "").strip()
        # Keep these in sync with your model constraints
        if len(username) < 4:
            raise ValidationError("Username must be at least 4 characters long.")
        if len(username) > 150:
            raise ValidationError("Username cannot exceed 150 characters.")
        # Only letters/digits/underscore:
        if not re.fullmatch(r"\w+", username):
            raise ValidationError("Username can only contain letters, digits, and underscores.")
        return username

    # Field-level validation (runs before clean())
    def clean_password1(self):
        password = self.cleaned_data.get("password1")
        if len(password) < 8:
            raise ValidationError("Password must be at least 8 characters long.")
        if not any(char.isdigit() for char in password):
            raise ValidationError("Password must contain at least one digit.")
        if not any(char.isupper() for char in password):
            raise ValidationError("Password must contain at least one uppercase letter.")
        if not any(char.islower() for char in password):
            raise ValidationError("Password must contain at least one lowercase letter.")
        if not any(char in "!@#$%^&*()_+-=[]{}|;:'\",.<>?/`~" for char in password):
            raise ValidationError("Password must contain at least one special character.")
        # TODO: check against common passwords list
        return password

    # Cross-field validation
    def clean(self):
        cleaned = super().clean()
        p1 = cleaned.get("password1")
        p2 = cleaned.get("password2")
        if p1 and p2 and p1 != p2:
            self.add_error("password2", "Passwords do not match.")
        return cleaned

    def save(self, commit=True):
        user = super().save(commit=False)
        user.password = make_password(self.cleaned_data["password1"])
        if commit:
            user.save()
        return user


class LoginForm(forms.Form):
    username = forms.CharField(max_length=200)
    password = forms.CharField(widget=forms.PasswordInput)

    def clean(self):
        cleaned = super().clean()
        uname = cleaned.get("username")
        pwd = cleaned.get("password")

        if not uname or not pwd:
            return cleaned  # field-level validators will flag empties

        try:
            user = User.objects.get(username=uname)
        except User.DoesNotExist:
            # Avoid leaking which field is wrong
            raise forms.ValidationError("Invalid username or password.")

        if not check_password(pwd, user.password):
            raise forms.ValidationError("Invalid username or password.")

        # stash the matched user for the view
        self.user = user
        return cleaned


class PostForm(forms.ModelForm):
    class Meta:
        model = Post
        fields = ("title", "content")
