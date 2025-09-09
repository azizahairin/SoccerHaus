# SoccerHaus

## Step 1: Membuat Proyek Django Baru dan Konfigurasi Environment Variables
1. Buat direktori proyek baru bernama soccerhaus.
2. Buat virtual environment: python3 -m venv env
3. mengaktifkan virtual environment: source env/bin/activate
4. Buat berkas requirements.txt dan isi dengan beberapa dependencies
5. lalu install: pip install -r requirements.txt
6. Buat proyek Django bernama soccerhaus: django-admin startproject soccerhaus .
7. Buat file .env dan .env.prod di direktori utama
8. Tambahkan konfigurasi di settings.py untuk menggunakan environment variables
9. Jalankan migrasi database: python3 manage.py migrate
10. Jalankan server django: python3 manage.py runserver

## Step 2: Aplikasi main
1. Pastikan virtual environment aktif
2. Jalankan: python manage.py startapp main
3. Daftarkan aplikasi main di INSTALLED_APPS pada settings.py
4. Membuat direktori baru bernama templates dan buat file main.html di dalamnya
5. isi file models.py yang berada di direktori aplikasi main
6. Jalankan migrasi: python3 manage.py makemigrations dan python3 manage.py migrate
7. Hubungkan views dengan template dengan isi data di views.py
8. modifikasi file main.html

## Step 3: Konfigurasi Routing URL
1. buat file urls.py di direktori main dan isi file tersebut
2. buka file urls.py yan berada di direktori proyek soccerhaus
3. Impor fungsi include dari django.urls.
4. Tambahkan rute URL path('', include('main.urls'))

## Push ke repositori Github
1. Buat repositori GitHub baru dengan visibilitas public
2. Inisiasi git: git init
3. Buat file .gitignore
4. Menghubungan repositori lokal dengan repositori GitHub yang baru dibuat: git remote add origin https://github.com/azizahairin/soccerhaus.git
5. buat branch utama: git branch -M master
6. add, commit, dan push dari direktori repositori lokal.
git add .
git commit -m "..."
git push origin master

## Deployment melalui PWS
1. Buat proyek baru di PWS, copy environment variables .env.prod
2. Tambahkan URL PWS ALLOWED_HOSTS di settings.py
3. Jalankan perintah pada informasi Project Command
4. Jalakan git push pws master setiap ada perubahan

## request client ke web aplikasi berbasis Django beserta responnya dan kaitan antara urls.py, views.py, models.py, dan berkas html.
Saat pengguna membuka web SoccerHaus, browser mengirimkan request ke server Django. Request pertama diterima oleh urls.py di level proyek, yang mengecek URL dan menentukan aplikasi mana yang akan menanganinya. Jika cocok, request diteruskan ke urls.py di aplikasi main, yang menghubungkan URL ke fungsi view yang tepat, misalnya show_main di views.py. View mengambil atau memproses data dari model (models.py) jika diperlukan, kemudian mengirim data tersebut ke template HTML (main.html). Model Product menyimpan informasi penting seperti name, price, description, thumbnail, category, dan is_featured. Template menampilkan data dari view ke halaman web, seperti detail produk atau data diri pengguna. Setelah itu, Django mengirimkan halaman HTML final ke browser, sehingga user bisa melihat tampilan web. 

Jadi, urls.py mengatur routing, views.py memproses data, models.py menyimpan struktur data, dan template HTML menampilkan tampilan akhir.

## peran settings.py dalam proyek Django
settings.py adalah berkas konfigurasi utama proyek Django yang mengatur semua pengaturan penting, mulai dari aplikasi apa saja yang dipakai, tempat nyimpen file, database. Misalnya, ALLOWED_HOSTS menentukan siapa saja yang boleh mengakses aplikasi. Jadi intinya, settings.py bikin proyek jalan dengan benar dan aman sesuai tempatnya: lokal atau server.

## cara kerja migrasi database di Django
Migrasi model adalah cara Django melacak setiap perubahan pada model basis data. Setiap kali menambahkan, menghapus, atau mengubah atribut di model, wajib melakukan migrasi agar perubahan tersebut tercermin di database. Untuk menjalankannya, pertama jalankan python manage.py makemigrations untuk membuat berkas migrasi, lalu jalankan python manage.py migrate untuk menerapkan perubahan tersebut ke database. Dengan begitu, struktur database selalu sesuai dengan model yang ada di kode. 

## framework Django dijadikan permulaan pembelajaran pengembangan perangkat lunak
Django dijadikan framework awal karena lengkap dan terstruktur. Banyak fitur yang sudah disediakan sehingga kita bisa langsung fokus memahami konsep inti pengembangan web, seperti MVT. Hal ini membuat alur data dan logika aplikasi lebih mudah dipahami tanpa harus membangun semuanya dari nol.

## Feedback untuk Asdos Tutorial 1
-
