# SoccerHaus

## Step 1: Membuat Proyek Django Baru dan Konfigurasi Environment Variables
1. Buat direktori proyek baru bernama soccerhaus.
2. Buat virtual environment: `python3 -m venv env`
3. mengaktifkan virtual environment: `source env/bin/activate`
4. Buat berkas requirements.txt dan isi dengan beberapa dependencies
5. lalu install: `pip install -r requirements.txt`
6. Buat proyek Django bernama soccerhaus: `django-admin startproject soccerhaus .`
7. Buat file `.env` dan `.env.prod` di direktori utama
8. Tambahkan konfigurasi di `settings.py` untuk menggunakan environment variables
9. Jalankan migrasi database: `python3 manage.py migrate`
10. Jalankan server django: `python3 manage.py runserver`

## Step 2: Aplikasi main
1. Pastikan virtual environment aktif
2. Jalankan: `python manage.py startapp main`
3. Daftarkan aplikasi main di `INSTALLED_APPS` pada `settings.py`
4. Membuat direktori baru bernama `templates` dan buat file `main.html` di dalamnya
5. isi file `models.py` yang berada di direktori aplikasi main
6. Jalankan migrasi: `python3 manage.py makemigrations` dan `python3 manage.py migrate`
7. Hubungkan views dengan template dengan isi data di `views.py`
8. modifikasi file `main.html`

## Step 3: Konfigurasi Routing URL
1. buat file `urls.py` di direktori main dan isi file tersebut
2. buka file `urls.py` yan berada di direktori proyek soccerhaus
3. Impor fungsi include dari `django.urls.`
4. Tambahkan rute URL `path('', include('main.urls'))`

## Push ke repositori Github
1. Buat repositori GitHub baru dengan visibilitas public
2. Inisiasi git: `git init`
3. Buat file `.gitignore`
4. Menghubungan repositori lokal dengan repositori GitHub yang baru dibuat: `git remote add origin https://github.com/azizahairin/soccerhaus.git`
5. buat branch utama: `git branch -M master`
6. add, commit, dan push dari direktori repositori lokal.
`git add .`,
`git commit -m "..."`,
`git push origin master`

## Deployment melalui PWS
1. Buat proyek baru di PWS, copy environment variables `.env.prod`
2. Tambahkan URL PWS ALLOWED_HOSTS di `settings.py`
3. Jalankan perintah pada informasi Project Command
4. Jalakan `git push pws master` setiap ada perubahan

## request client ke web aplikasi berbasis Django beserta responnya dan kaitan antara urls.py, views.py, models.py, dan berkas html.
![alt text](image.png)
Saat pengguna membuka web SoccerHaus melalui browser, browser mengirim request ke server Django. Request pertama-tama dicek oleh urls.py di level proyek, yang menentukan aplikasi mana yang akan menangani request tersebut. Jika URL sesuai, request diteruskan ke urls.py di level aplikasi untuk memanggil view function yang cocok, seperti show_main di views.py.

views.py menerima request tersebut, lalu mengambil atau memproses data dari models.py. Misalnya, model Product menyimpan atribut seperti name, price, description, thumbnail, category, dan is_featured. Setelah data siap, view mengirimnya ke template HTML yang akan menampilkan informasi tersebut di halaman web. Template hanya menampilkan data yang dikirim view menggunakan template variables.

Hasil render template ini kemudian dikirim sebagai response ke browser, sehingga user bisa melihat tampilan web. Jadi, urls.py mengatur routing, views.py memproses data, models.py menyimpan struktur data, dan template HTML menampilkan tampilan akhir.

## peran settings.py dalam proyek Django
settings.py adalah berkas konfigurasi utama proyek Django yang mengatur semua pengaturan penting, mulai dari aplikasi apa saja yang dipakai, tempat nyimpen file, database. Misalnya, ALLOWED_HOSTS menentukan siapa saja yang boleh mengakses aplikasi. Jadi intinya, settings.py bikin proyek jalan dengan benar dan aman sesuai tempatnya: lokal atau server.

## cara kerja migrasi database di Django
Migrasi model adalah cara Django melacak setiap perubahan pada model basis data. Setiap kali menambahkan, menghapus, atau mengubah atribut di model, wajib melakukan migrasi agar perubahan tersebut tercermin di database. Untuk menjalankannya, pertama jalankan python manage.py makemigrations untuk membuat berkas migrasi, lalu jalankan python manage.py migrate untuk menerapkan perubahan tersebut ke database. Dengan begitu, struktur database selalu sesuai dengan model yang ada di kode. 

## framework Django dijadikan permulaan pembelajaran pengembangan perangkat lunak
Django dijadikan framework awal karena lengkap dan terstruktur. Banyak fitur yang sudah disediakan sehingga kita bisa langsung fokus memahami konsep inti pengembangan web, seperti MVT. Hal ini membuat alur data dan logika aplikasi lebih mudah dipahami tanpa harus membangun semuanya dari nol.

## Feedback untuk Asdos Tutorial 1
-

## Fungsi data delivery alam pengimplementasian sebuah platform
Kita memerlukan data delivery dalam sebuah platform karena setiap aplikasi seringkali membutuhkan cara yang berbeda untuk mengakses dan menggunakan data. Misalnya, pengguna biasa membutuhkan tampilan data dalam bentuk HTML agar mudah dibaca di browser, sementara aplikasi lain atau layanan eksternal mungkin membutuhkan data mentah dalam format JSON atau XML untuk diproses lebih lanjut. Dengan adanya data delivery, data dapat dibagikan dengan fleksibel sesuai kebutuhan, sehingga platform menjadi lebih mudah diintegrasikan, lebih efisien, dan bisa digunakan di berbagai perangkat atau sistem.

## Mana yang lebih baik antara XML dan JSON? Mengapa JSON lebih populer dibandingkan XML?
Secara umum, JSON dianggap lebih baik dibandingkan XML untuk kebutuhan pertukaran data modern. Hal ini karena JSON lebih sederhana, lebih mudah dibaca manusia, dan ukurannya lebih ringan sehingga lebih cepat diproses. JSON juga terintegrasi dengan baik pada hampir semua bahasa pemrograman, terutama JavaScript yang menjadi dasar pengembangan web. XML memang memiliki kelebihan dalam hal struktur yang ketat dan dukungan untuk dokumen yang kompleks, tetapi sering kali terasa terlalu berat dan bertele-tele untuk kebutuhan aplikasi web atau mobile. 

## Fungsi method is_valid() pada form Django
Method is_valid() pada form Django berfungsi untuk memeriksa apakah data yang dimasukkan ke dalam form sudah sesuai dengan aturan dan tipe data yang ditentukan di model atau form itu sendiri. Misalnya, kalau sebuah field harus angka tapi user mengisi teks, maka is_valid() akan mengembalikan false. Dengan begitu, kita bisa mencegah data yang salah atau tidak lengkap tersimpan ke database. Method ini penting karena membantu menjaga konsistensi dan keakuratan data, serta membuat aplikasi lebih aman dan terkontrol.

## Fungsi csrf_token
Kita membutuhkan csrf_token pada form Django untuk melindungi aplikasi dari serangan CSRF (Cross-Site Request Forgery). Token ini berfungsi sebagai tanda pengenal unik yang memastikan bahwa permintaan form benar-benar berasal dari pengguna yang sah, bukan dari pihak luar. Jika csrf_token tidak ditambahkan, penyerang bisa memanfaatkan celah ini dengan membuat halaman berbahaya yang diam-diam mengirim permintaan ke server kita menggunakan akun pengguna yang sedang login. Akibatnya, penyerang bisa melakukan aksi tanpa izin, seperti mengubah data atau mengirim informasi penting. Dengan adanya csrf_token, Django dapat memverifikasi setiap permintaan form dan mencegah serangan tersebut.

## Step-by-step Implementasi Checklist
1. Buat templates/base.html di root project (satu level dengan manage.py) sebagai layout utama.
2. Konfigurasi settings.py: TEMPLATES['DIRS'] = [BASE_DIR / 'templates'] dan  APP_DIRS=True.
3. Model: pakai Product di main/models.py (name, price, description, thumbnail (URL), category, is_featured).
4. Form: buat main/forms.py: ProductForm (ModelForm) dengan fields: ["name","price","description","thumbnail","category","is_featured"].
5. Views (main/views.py)
    show_main: ambil Product.objects.all() dan kirim ke template.
    create_product: tampilkan form (GET) & simpan produk baru (POST) dengan is_valid(), lalu redirect ke list.
    show_product(id): ambil 1 produk by PK dan render halaman detail.
6. Templates
    main/templates/main.html: tampilkan daftar produk, tombol “+ Add Product” dan tombol/link “View Detail”
    main/templates/create_product.html: form tambah produk, pakai {% csrf_token %} untuk keamanan CSRF.
    main/templates/product_detail.html: tampilkan field produk (name, price, category, is_featured, thumbnail, description).
7. Data Delivery: XML & JSON 
    Imports di main/views.py: from django.http import HttpResponse dan from django.core import serializers.
    show_xml: Product.objects.all() ke XML + content_type="application/xml".
    show_json: Product.objects.all() ke JSON + content_type="application/json".
    show_xml_by_id(id): Product.objects.get(pk=id), content_type="application/xml".
    show_json_by_id(id): sama seperti XML, tapi "json".
8. Routing URL
main/urls.py:

app_name = 'main'
urlpatterns = [
    path('', show_main, name='show_main'),
    path('create-product/', create_product, name='create_product'),
    path('product/<int:id>/', show_product, name='show_product'),
    path('xml/', show_xml, name='show_xml'),
    path('json/', show_json, name='show_json'),
    path('xml/<int:id>/', show_xml_by_id, name='show_xml_by_id'),
    path('json/<int:id>/', show_json_by_id, name='show_json_by_id'),
]
9. Jalanin server: python manage.py runserver.

## Feedback untuk Asdos Tutorial 2
tidak ada

## screenshot postman
![alt text](image-1.png)
![alt text](<Screenshot 2025-09-16 at 20.42.30.png>) ![alt text](<Screenshot 2025-09-16 at 20.42.23.png>) ![alt text](<Screenshot 2025-09-16 at 20.41.15.png>)
