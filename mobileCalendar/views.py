from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
from .models import Event
from django.core import serializers
from datetime import datetime
from django import forms
from django.views.decorators.csrf import csrf_exempt

def index(request):
    return HttpResponse("Hello, world!")

@csrf_exempt
def addNewEvent(request):
    if request.method == 'POST':
        form = EventForm(request.POST)
        if form.is_valid():
            new_event = form.save()
            return HttpResponse("Create new event success")
    else:
        return HttpResponse("Please use POST method")

@csrf_exempt
def finishEvent(request):
    if request.method == 'POST':
        id = request.POST['id']
        Event.objects.filter(pk=id).update(is_finished=True)
        return HttpResponse("Create new event success")
    else:
        return HttpResponse("Please use POST method")


def getAllEvent(request):
    allEvents = serializers.serialize("json", Event.objects.all().order_by("expected_date"))
    return HttpResponse(allEvents, content_type="application/json")

class EventForm(forms.ModelForm):
    class Meta:
        model = Event
        fields = "__all__"