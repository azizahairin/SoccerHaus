from django.shortcuts import render

def show_main(request):
    context = {
        'name': 'Azizah Khairinniswah',
        'class': 'PBP F',
    }

    return render(request, "main.html", context)