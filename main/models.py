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

    name = models.CharField(max_length=255)  # Nama produk
    price = models.IntegerField()  # Harga produk
    description = models.TextField()  # Deskripsi produk
    thumbnail = models.URLField(blank=True, null=True)  # Link gambar produk
    category = models.CharField(max_length=20, choices=CATEGORY_CHOICES, default='jersey')  # Kategori produk
    is_featured = models.BooleanField(default=False)  # Produk unggulan atau bukan

    def __str__(self):
        return f"{self.name} - {self.category}"
