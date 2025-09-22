import uuid
from django.db import models

class Product(models.Model):
    CATEGORY_CHOICES = [
        ('jersey', 'Jersey'),
        ('shoes', 'Shoes'),
        ('ball', 'Ball'),
        ('accessories', 'Accessories'),
        ('equipment', 'Equipment'),
    ]

    name = models.CharField(max_length=255) 
    price = models.IntegerField()  
    description = models.TextField()  
    thumbnail = models.URLField(blank=True, null=True)  
    category = models.CharField(max_length=20, choices=CATEGORY_CHOICES, default='jersey')  
    is_featured = models.BooleanField(default=False)  


    def __str__(self):
        return f"{self.name} - {self.category}"
    