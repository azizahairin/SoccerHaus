from django.contrib.auth.forms import UserCreationForm, AuthenticationForm
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.urls import reverse
from django.core import serializers
from django.shortcuts import render, redirect, get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
from main.forms import ProductForm
from main.models import Product
from django.utils import timezone
from django.utils.html import strip_tags
import requests
import json

@login_required(login_url='/login')
def show_main(request):
    filter_type = request.GET.get("filter", "all")
    category = request.GET.get("category")

    if filter_type == "my":
        products = Product.objects.filter(user=request.user)
    else:
        products = Product.objects.all()

    if category:
        products = products.filter(category=category)

    context = {
        'name': 'Azizah Khairinniswah',
        'class': 'PBP F',
        'products': products,
        'last_login': request.COOKIES.get('last_login', 'Never'),
        'CATEGORY_CHOICES': [
            ('jersey', 'Jersey'),
            ('shoes', 'Shoes'),
            ('ball', 'Ball'),
            ('accessories', 'Accessories'),
            ('equipment', 'Equipment'),
        ],
    }
    return render(request, "main.html", context)

def create_product(request):
    form = ProductForm(request.POST or None)
    if form.is_valid() and request.method == "POST":
        product_entry = form.save(commit=False)
        product_entry.user = request.user
        product_entry.save()
        return redirect('main:show_main')
    return render(request, "create_product.html", {'form': form})

@login_required(login_url='/login')
def show_product(request, id):
    product = get_object_or_404(Product, pk=id)
    return render(request, "product_detail.html", {'product': product})

def show_xml(request):
    products = Product.objects.all()
    data = serializers.serialize("xml", products)
    return HttpResponse(data, content_type="application/xml")

def show_json(request):
    qs = Product.objects.all()
    filter_type = request.GET.get("filter")
    category = request.GET.get("category")
    if filter_type == "my" and request.user.is_authenticated:
        qs = qs.filter(user=request.user)
    if category:
        qs = qs.filter(category=category)
    data = [
        {
            "id": p.id,
            "title": p.name,
            "content": p.description,
            "category": p.category,
            "thumbnail": p.thumbnail,
            "price": p.price,
            "is_featured": p.is_featured,
            "user_id": p.user_id,
        } for p in qs
    ]
    return JsonResponse(data, safe=False)

def show_xml_by_id(request, id):
    try:
        obj = Product.objects.get(pk=id)
        xml_data = serializers.serialize("xml", [obj])
        return HttpResponse(xml_data, content_type="application/xml")
    except Product.DoesNotExist:
        return HttpResponse(status=404)

def show_json_by_id(request, id):
    try:
        p = Product.objects.select_related('user').get(pk=id)
        data = {
            "id": p.id,
            "title": p.name,
            "content": p.description,
            "category": p.category,
            "thumbnail": p.thumbnail,
            "price": p.price,
            "is_featured": p.is_featured,
            "user_id": p.user_id,
            "user_username": p.user.username if p.user_id else None,
        }
        return JsonResponse(data)
    except Product.DoesNotExist:
        return JsonResponse({"detail": "Not found"}, status=404)

@login_required(login_url='/login')
@require_POST
def delete_product(request, id):
    product = get_object_or_404(Product, pk=id, user=request.user)
    product.delete()
    messages.success(request, "Product berhasil dihapus.")
    return redirect('main:show_main')

def register(request):
    form = UserCreationForm()
    if request.method == "POST":
        form = UserCreationForm(request.POST)
        if form.is_valid():
            form.save()
            messages.success(request, 'Your account has been successfully created!')
            return redirect('main:login')
    return render(request, 'register.html', {'form':form})

def login_user(request):
    if request.method == 'POST':
        form = AuthenticationForm(data=request.POST)
        if form.is_valid():
            user = form.get_user()
            prev = user.last_login
            login(request, user)
            response = HttpResponseRedirect(reverse("main:show_main"))
            if prev:
                response.set_cookie('last_login', timezone.localtime(prev).strftime("%d %b %Y, %H:%M"))
            else:
                response.set_cookie('last_login', 'Never')
            return response
    else:
        form = AuthenticationForm(request)
    return render(request, 'login.html', {'form': form})

def logout_user(request):
    logout(request)
    response = HttpResponseRedirect(reverse('main:login'))
    response.delete_cookie('last_login')
    return response

def edit_product(request, id):
    product = get_object_or_404(Product, pk=id)
    form = ProductForm(request.POST or None, instance=product)
    if form.is_valid() and request.method == "POST":
        form.save()
        return redirect('main:show_main')
    return render(request, "edit_product.html", {'form': form})

@login_required(login_url='/login')
@csrf_exempt  
@require_POST
def add_product_entry_ajax(request):
    name        = strip_tags(request.POST.get("name"))
    description = strip_tags(request.POST.get("description"))
    category    = request.POST.get("category")
    thumbnail   = request.POST.get("thumbnail") or None
    is_featured = (request.POST.get("is_featured") == 'on')
    price_raw   = request.POST.get("price")
    try:
        price = int(price_raw) if price_raw is not None else 0
    except ValueError:
        return JsonResponse({"detail": "Price must be an integer"}, status=400)

    Product.objects.create(
        user=request.user,
        name=name,
        description=description,
        category=category,
        thumbnail=thumbnail,
        is_featured=is_featured,
        price=price,
    )
    return HttpResponse(b"CREATED", status=201)

@login_required(login_url='/login')
@require_POST
def update_product_entry_ajax(request, id):
    p = get_object_or_404(Product, pk=id, user=request.user)
    name        = strip_tags(request.POST.get("name"))
    description = strip_tags(request.POST.get("description"))
    category    = request.POST.get("category")
    thumbnail   = request.POST.get("thumbnail") or None
    is_featured = (request.POST.get("is_featured") == 'on')
    price_raw   = request.POST.get("price")
    try:
        price = int(price_raw) if price_raw is not None else p.price or 0
    except ValueError:
        return JsonResponse({"detail": "Price must be an integer"}, status=400)

    # Minimal validation
    if not name or not description or not category:
        return JsonResponse({"detail": "Missing required fields"}, status=400)

    p.name = name
    p.description = description
    p.category = category
    p.thumbnail = thumbnail
    p.is_featured = is_featured
    p.price = price
    p.save()
    return JsonResponse({"detail": "UPDATED", "id": p.id})

@login_required(login_url='/login')
@require_POST
def delete_product_ajax(request, id):
    p = get_object_or_404(Product, pk=id, user=request.user)
    p.delete()
    return JsonResponse({"detail": "DELETED"})

@require_POST
def register_ajax(request):
    form = UserCreationForm(request.POST)
    if form.is_valid():
        user = form.save()
        return JsonResponse({"ok": True, "message": "Account created."}, status=201)
    return JsonResponse({"ok": False, "errors": form.errors}, status=400)

@require_POST
def login_ajax(request):
    form = AuthenticationForm(data=request.POST)
    if form.is_valid():
        user = form.get_user()
        prev = user.last_login
        login(request, user)
        resp = JsonResponse({"ok": True, "message": "Logged in."})
        if prev:
            resp.set_cookie('last_login', timezone.localtime(prev).strftime("%d %b %Y, %H:%M"))
        else:
            resp.set_cookie('last_login', 'Never')
        return resp
    return JsonResponse({"ok": False, "errors": form.errors}, status=400)

@login_required(login_url='/login')
@require_POST
def logout_ajax(request):
    logout(request)
    resp = JsonResponse({"ok": True, "message": "Logged out."})
    resp.delete_cookie('last_login')
    return resp

def proxy_image(request):
    image_url = request.GET.get('url')
    if not image_url:
        return HttpResponse('No URL provided', status=400)
    
    try:
        # Fetch image from external source
        response = requests.get(image_url, timeout=10)
        response.raise_for_status()
        
        # Return the image with proper content type
        return HttpResponse(
            response.content,
            content_type=response.headers.get('Content-Type', 'image/jpeg')
        )
    except requests.RequestException as e:
        return HttpResponse(f'Error fetching image: {str(e)}', status=500)
    
@csrf_exempt
def create_product_flutter(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)

            title       = strip_tags(data.get("title", ""))
            content     = strip_tags(data.get("content", ""))
            category    = data.get("category", "")
            thumbnail   = data.get("thumbnail", "")
            is_featured = data.get("is_featured", False)
            price       = data.get("price", 0)
            user        = request.user if request.user.is_authenticated else None

            new_product = Product(
                name=title,              
                description=content,     
                category=category,
                thumbnail=thumbnail or None,
                is_featured=is_featured,
                price=price,
                user=user,
            )
            new_product.save()

            return JsonResponse({"status": "success"}, status=200)

        except Exception as e:
            return JsonResponse(
                {"status": "error", "message": str(e)},
                status=400,
            )

    return JsonResponse(
        {"status": "error", "message": "Invalid request method."},
        status=405,
    )
