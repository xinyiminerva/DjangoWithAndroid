from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('getAllEvent', views.getAllEvent, name='getAllEvent'),
    path('addNewEvent', views.addNewEvent, name='addNewEvent'),
    path('finishEvent', views.finishEvent, name='finishEvent'),
]