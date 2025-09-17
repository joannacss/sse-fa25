from django.urls import path

from . import views
app_name = 'blog'  # creates a namespace for this app
urlpatterns = [
    path('', views.index, name='index'),
    path('register/', views.register, name='register'),
]
