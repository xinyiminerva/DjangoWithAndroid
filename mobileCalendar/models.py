from django.db import models

# Create your models here.
class Event(models.Model):
    event_title = models.CharField(max_length=200)
    event_category = models.CharField(max_length=200)
    event_text = models.CharField(max_length=200)
    due_date = models.DateTimeField('due date')
    expected_date = models.DateTimeField('date expected')
    is_finished = models.BooleanField()

    def __str__(self):
        return self.event_text