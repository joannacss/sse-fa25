from django import forms
from .models import User

from django import forms
from django.contrib.auth.hashers import make_password
from .models import User


class RegisterForm(forms.ModelForm):
    password1 = forms.CharField(label="Password", widget=forms.PasswordInput)
    password2 = forms.CharField(label="Confirm password", widget=forms.PasswordInput)

    class Meta:
        model = User
        fields = ("username", "email")  # password handled separately

    def clean(self):
        cleaned_data = super().clean()
        p1 = cleaned_data.get("password1")
        p2 = cleaned_data.get("password2")
        if p1 and p2 and p1 != p2:
            self.add_error("password2", "Passwords do not match.")
        else:
            self.validate_password()

        return cleaned_data

    def validate_password(self):
        password = self.cleaned_data.get("password1")
        if len(password) < 8:
            self.add_error("password1", "Password must be at least 8 characters long.")
        if not any(char.isdigit() for char in password):
            self.add_error("password1", "Password must contain at least one digit.")
        if not any(char.isupper() for char in password):
            self.add_error("password1", "Password must contain at least one uppercase letter.")
        if not any(char.islower() for char in password):
            self.add_error("password1", "Password must contain at least one lowercase letter.")
        if not any(char in "!@#$%^&*()_+-=[]{}|;:'\",.<>?/`~" for char in password):
            self.add_error("password1", "Password must contain at least one special character.")
        # TODO: check against common passwords list

        return password

    def save(self, commit=True):
        user = super().save(commit=False)
        user.password = make_password(self.cleaned_data["password1"])
        if commit:
            user.save()
        return user
