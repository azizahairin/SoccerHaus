# main/forms.py
from django import forms
from .models import Product

INPUT = "w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#3F5A83]"
TEXTAREA = "w-full rounded-md border border-gray-300 px-3 py-2 h-32 focus:outline-none focus:ring-2 focus:ring-[#3F5A83]"
SELECT = "w-full rounded-md border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#3F5A83]"
CHECKBOX = "w-5 h-5 rounded border-gray-300 text-[#3F5A83] focus:ring-[#3F5A83]"

class ProductForm(forms.ModelForm):
    class Meta:
        model = Product
        fields = ["name", "price", "description", "thumbnail", "category", "is_featured"]
        widgets = {
            "name": forms.TextInput(attrs={"class": INPUT, "placeholder": "e.g. Nike Tiempo"}),
            "price": forms.NumberInput(attrs={"class": INPUT, "placeholder": "e.g. 250000"}),
            "description": forms.Textarea(attrs={"class": TEXTAREA, "placeholder": "Tell more about the product..."}),
            "thumbnail": forms.URLInput(attrs={"class": INPUT, "placeholder": "https://..."}),
            "category": forms.Select(attrs={"class": SELECT}),
            "is_featured": forms.CheckboxInput(attrs={"class": CHECKBOX}),
        }
