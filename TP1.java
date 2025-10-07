import java.io.*;
import java.util.*;

public class TP1 {

    // ===== Fast Scanner (biar IO cepet) =====
    static class FS {
        InputStream in; byte[] b = new byte[1<<16]; int p=0, n=0;
        FS(InputStream is){ in=is; }
        int r() throws IOException { if(p>=n){ n=in.read(b); p=0; if(n<=0) return -1; } return b[p++]; }
        String next() throws IOException {
            StringBuilder sb=new StringBuilder(); int c;
            // skip whitespace
            do{ c=r(); }while(c<=32 && c!=-1);
            if(c==-1) return null;
            // ambil token sampai ketemu spasi lagi
            while(c>32){ sb.append((char)c); c=r(); }
            return sb.toString();
        }
        int nextInt() throws IOException { return Integer.parseInt(next()); }
        long nextLong() throws IOException { return Long.parseLong(next()); }
    }

    // ===== Model status lokasi antrean =====
    static final int NONE=0, AP=1, AK=2; // NONE=keluar, AP=Antre Penyampaian, AK=Antre Konsumsi

    // ===== Data orang (D) =====
    static class D {
        long base, urg, join;   // base: energi saat gabung; urg: urgensi; join: tick saat gabung
        int where;              // posisi sekarang (NONE/AP/AK)
        boolean alive;          // masih aktif di sistem
    }

    // ===== Key buat PriorityQueue AP =====
    // Urutan: (base + join) naik -> efektif energi turun lebih dulu
    // tie-breaker: urgensi besar dulu (makanya disimpan -urg), lalu id naik
    static class Key implements Comparable<Key>{
        long k1, k2; int id;
        Key(long a,long b,int c){ k1=a; k2=b; id=c; }
        public int compareTo(Key o){
            if(k1!=o.k1) return Long.compare(k1,o.k1);
            if(k2!=o.k2) return Long.compare(k2,o.k2);
            return Integer.compare(id,o.id);
        }
    }

    // ===== Globals =====
    static int N,M,Q, nextId=0;
    static long tick=0;                 // waktu global; tiap aksi tertentu menambah tick
    static long[] eat, cost, val;       // eat: konsumsi fixed; cost/val: item DP
    static ArrayList<D> ds = new ArrayList<>();
    static PriorityQueue<Key> pq = new PriorityQueue<>(); // antrean AP (pakai PQ)
    static ArrayDeque<Integer> ak = new ArrayDeque<>();   // antrean AK (FIFO)
    static ArrayDeque<Long> ban = new ArrayDeque<>();     // tumpukan "spanduk" (stack)

    // ===== Util: energi efektif saat ini =====
    static long eff(int id){ D d=ds.get(id); return d.base - (tick - d.join); }

    // Masuk AP (priority queue) dengan key sesuai aturan
    static void pushAP(int id){
        D d=ds.get(id); d.where=AP; d.join=tick;
        pq.add(new Key(d.base + d.join, -d.urg, id));
    }

    // Bersihin head AP dari entri mati / tidak valid / energi habis
    static void cleanAP(){
        while(!pq.isEmpty()){
            Key k=pq.peek();
            if(k.id>=ds.size()){ pq.poll(); continue; }
            D d=ds.get(k.id);
            if(!d.alive || d.where!=AP){ pq.poll(); continue; }
            if(eff(k.id)<=0){ d.alive=false; d.where=NONE; pq.poll(); continue; }
            break; // head sudah valid
        }
    }

    // Bersihin head AK dari entri mati / tidak valid / energi habis
    static void cleanAK(){
        while(!ak.isEmpty()){
            int id = ak.peekFirst();
            if(id>=ds.size()){ ak.pollFirst(); continue; }
            D d = ds.get(id);
            if(!d.alive || d.where!=AK){ ak.pollFirst(); continue; }
            if(eff(id)<=0){ d.alive=false; d.where=NONE; ak.pollFirst(); continue; }
            break; // head sudah valid
        }
    }

    // Pop satu id valid dari AK (lewati yang invalid)
    static int popAK(){
        while(!ak.isEmpty()){
            int id=ak.pollFirst();
            if(id>=ds.size()) continue;
            D d=ds.get(id);
            if(!d.alive || d.where!=AK) continue;
            if(eff(id)<=0){ d.alive=false; d.where=NONE; continue; }
            return id; // ketemu yang valid
        }
        return -1; // kosong/ga ada yang valid
    }

    // Pilih konsumsi terbesar di rentang [L..U] (eat[] sudah terurut naik)
    static long pickEat(long L, long U){
        int lo=0, hi=N;
        // upper_bound(U)
        while(lo<hi){ int md=(lo+hi)>>>1; if(eat[md]<=U) lo=md+1; else hi=md; }
        int idx=lo-1;
        return (idx>=0 && eat[idx]>=L)? eat[idx] : 0L;
    }

    // ====== DP "Poster" versi O (hanya nilai maks, cache by capacity) ======
    static int oBuiltE = -1;          // E maksimum yang sudah dihitung/cached
    static long[] oCache0 = null;     // cache jawaban s=0,i=0 untuk semua e (0..E)

    // buffer layer (hemat alokasi ulang)
    static long[] oN0, oN1, oN2;      // next (i+1)
    static long[] oC0, oC1, oC2;      // cur  (i)

    static void ensureOCap(int E){
        int L = E + 1;
        if (oN0 == null || oN0.length < L){
            oN0 = new long[L]; oN1 = new long[L]; oN2 = new long[L];
            oC0 = new long[L]; oC1 = new long[L]; oC2 = new long[L];
        }
        if (oCache0 == null || oCache0.length < L){
            oCache0 = new long[L];
        }
    }

    // solveO: knapsack dengan state s (0/1/2 kali berturut-turut) → nilai maks; return hanya dp[0,0,E]
    static long solveO(long X){
        final int E = (int)X;
        // kalau sudah pernah build sampai E ini, langsung ambil cache
        if (E <= oBuiltE && oCache0 != null) return oCache0[E];

        ensureOCap(E);
        long[] nxt0 = oN0, nxt1 = oN1, nxt2 = oN2;
        long[] cur0 = oC0, cur1 = oC1, cur2 = oC2;

        // base case: i = M → 0 untuk semua e & s
        Arrays.fill(nxt0, 0, E+1, 0L);
        Arrays.fill(nxt1, 0, E+1, 0L);
        Arrays.fill(nxt2, 0, E+1, 0L);

        // proses item dari belakang
        for(int i=M-1;i>=0;i--){
            final int c = (int)cost[i];
            final long v = val[i];
            final long v2 = v >>> 1; // kalau diambil sebagai ke-2 berturut-turut, nilai setengah

            // kasus e < c: cuma bisa skip
            int lim = Math.min(c, E+1);
            for(int e=0;e<lim;e++){
                long skip = nxt0[e];
                cur0[e] = skip; // s=0
                cur1[e] = skip; // s=1
                cur2[e] = skip; // s=2
            }
            // kasus e >= c: boleh take/skip
            for(int e=c;e<=E;e++){
                long skip = nxt0[e];
                long t0 = v  + nxt1[e-c];  // ambil sebagai urutan ke-1 (s:0→1)
                long t1 = v2 + nxt2[e-c];  // ambil sebagai urutan ke-2 (s:1→2)
                cur0[e] = (t0>skip? t0:skip);
                cur1[e] = (t1>skip? t1:skip);
                cur2[e] = skip;           // s=2 wajib reset (ga boleh ambil)
            }

            // swap layer (cur → next)
            long[] t;
            t=nxt0; nxt0=cur0; cur0=t;
            t=nxt1; nxt1=cur1; cur1=t;
            t=nxt2; nxt2=cur2; cur2=t;
        }

        // simpan semua dp[0,0,e] ke cache biar query berikutnya O(1)
        if (oCache0.length < E+1) oCache0 = Arrays.copyOf(oCache0, E+1);
        System.arraycopy(nxt0, 0, oCache0, 0, E+1);
        oBuiltE = E;
        return oCache0[E];
    }

    // ====== DP "Poster" versi P (rekonstruksi pilihan) ======
    static String solveP(long X){
        final int E = (int)X;
        final int W = E + 1;
        final int ROW = (M+1)*W;

        // dp untuk semua i,s,e (disimpan flatten)
        long[] dp0 = new long[ROW];  // s=0
        long[] dp1 = new long[ROW];  // s=1
        long[] dp2 = new long[ROW];  // s=2

        // build bottom-up
        for(int i=M-1;i>=0;i--){
            int base = i*W, next = (i+1)*W;
            int c = (int)cost[i];
            long v = val[i], v2 = v>>>1;

            // e < c → skip
            int lim = Math.min(c, E+1);
            for(int e=0;e<lim;e++){
                long skip = dp0[next + e];
                dp0[base + e] = skip;
                dp1[base + e] = skip;
                dp2[base + e] = skip;
            }
            // e >= c → bisa take
            for(int e=c;e<=E;e++){
                long skip = dp0[next + e];
                long t0 = v  + dp1[next + (e-c)]; // s 0→1
                long t1 = v2 + dp2[next + (e-c)]; // s 1→2
                dp0[base + e] = (t0>skip? t0:skip);
                long cand1 = (t1>skip? t1:skip);
                dp1[base + e] = cand1;
                dp2[base + e] = skip; // s=2 harus skip
            }
        }

        // rekonstruksi pilihan dari (i=0,s=0,e=E)
        long best = dp0[E];
        int s=0, e=E;
        ArrayList<Integer> pick = new ArrayList<>();
        for(int i=0;i<M;i++){
            int curIdx = i*W + e, next = (i+1)*W;
            long cur = (s==0? dp0[curIdx] : s==1? dp1[curIdx] : dp2[curIdx]);
            long skip = dp0[next + e];

            boolean canTake = false;
            int c = (int)cost[i];
            if(s<=1 && e>=c){
                long add = (s==0)? val[i] : (val[i]>>>1);
                long tail = (s==0)? dp1[next + (e-c)] : dp2[next + (e-c)];
                long take = add + tail;
                if(take >= skip && take == cur) canTake = true; // cocok dengan nilai optimal
            }

            if(canTake){
                pick.add(i);
                e -= (int)cost[i];
                s += 1; // lanjut urutan ambil
            }else{
                s = 0;  // reset urutan kalau skip
            }
        }

        // format output: best diikuti index yang dipilih (urut i)
        StringBuilder sb = new StringBuilder();
        sb.append(best);
        for(int idx: pick) sb.append(' ').append(idx);
        return sb.toString();
    }

    // ====== Main driver ======
    public static void main(String[] a) throws Exception{
        FS in=new FS(System.in);
        String s0=in.next(); if(s0==null) return;
        N=Integer.parseInt(s0); M=in.nextInt(); Q=in.nextInt();

        // input data
        eat=new long[N]; for(int i=0;i<N;i++) eat[i]=in.nextLong();
        cost=new long[M]; for(int i=0;i<M;i++) cost[i]=in.nextLong();
        val =new long[M]; for(int i=0;i<M;i++) val[i]=in.nextLong();

        StringBuilder out=new StringBuilder();

        // proses Q query
        for(int qi=0; qi<Q; qi++){
            String cmd=in.next(); char c=cmd.charAt(0);

            if(c=='A'){ // A e u : orang masuk AP
                long e=in.nextLong(), u=in.nextLong();
                D d=new D(); d.base=e; d.urg=u; d.join=tick; d.where=AP; d.alive=true;
                ds.add(d); int id=nextId++;
                pq.add(new Key(d.base + d.join, -d.urg, id));
                out.append(id).append('\n');

            }else if(c=='S'){ // S x : tambah spanduk (stack)
                long x=in.nextLong(); ban.addLast(x);
                out.append(ban.size()).append('\n');

            }else if(c=='B'){ // B : coba sampaikan aspirasi (ambil dari AP teratas)
                cleanAP();
                cleanAK();

                if(pq.isEmpty()){ out.append("-1\n"); continue; }

                int id=-1;
                // ambil kandidat valid dari PQ
                while(!pq.isEmpty()){
                    Key k=pq.poll();
                    if(k.id>=ds.size()) continue;
                    D d=ds.get(k.id);
                    if(!d.alive || d.where!=AP) continue;
                    if(eff(k.id)<=0){ d.alive=false; d.where=NONE; continue; }
                    id=k.id; break;
                }
                if(id==-1){ out.append("-1\n"); continue; }

                D d=ds.get(id);
                long e=eff(id);
                // kalau ada spanduk, kurangi efeknya dari energi
                if(!ban.isEmpty()){ e -= ban.removeLast(); }

                if(e<=0){ // gagal sampaikan
                    out.append(id).append('\n');
                    d.alive=false; d.where=NONE;
                    tick++;             // semua yang nunggu kena penalti -1
                    cleanAP(); cleanAK();
                }else{    // sukses → pindah ke AK
                    out.append(e).append('\n');
                    tick++;             // penalti global
                    d.base=e; d.where=AK; d.join=tick; // join AK setelah tick
                    ak.addLast(id);
                    cleanAP(); cleanAK();
                }

            }else if(c=='K'){ // K L U : konsumsi dari AK lalu balik ke AP
                long L=in.nextLong(), U=in.nextLong();
                cleanAK();
                int id=popAK();
                if(id==-1){ out.append("-1\n"); continue; }
                long take=pickEat(L,U); // pilih konsumsi terbaik dalam range
                out.append(take).append('\n');
                D d=ds.get(id);
                long e=eff(id); if(take>0) e+=take; // energi nambah kalau ada konsumsi
                d.base=e; pushAP(id);              // balik ke AP
                cleanAP(); cleanAK();

            }else if(c=='L'){ // L id : pulang dari sistem (lapor posisi & energi)
                int id=in.nextInt();
                if(id<0 || id>=nextId){ out.append("-1\n"); continue; }
                D d=ds.get(id);
                if(!d.alive || d.where==NONE){ out.append("-1\n"); continue; }
                long e=eff(id);
                if(e<=0){
                    d.alive=false; d.where=NONE;
                    out.append("-1\n");
                    cleanAP(); cleanAK();
                    continue;
                }
                // print kode lokasi (1=AP, 2=AK) + energi terkini
                if(d.where==AP) out.append("1 ").append(e).append('\n');
                else            out.append("2 ").append(e).append('\n');
                d.alive=false; d.where=NONE;
                cleanAP(); cleanAK();

            } else if(c=='O'){ // O X : DP nilai maksimum (tanpa rekonstruksi) dgn kapasitas X
                long X=in.nextLong();
                out.append(solveO(X)).append('\n');
            
            } else if(c=='P'){ // P X : DP + rekonstruksi (print best dan index terpilih)
                long X=in.nextLong();
                out.append(solveP(X)).append('\n');
            }
        }

        // output semua jawaban
        System.out.print(out.toString());
    }
}
