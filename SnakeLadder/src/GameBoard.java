import java.util.*;

public class GameBoard {
    private final int rows, cols;
    private final int totalSquares;
    private final Map<Integer, Link> links;
    private final Set<Integer> starPositions;     // Magic Bubble
    private final Set<Integer> primePositions;    // Untuk Dijkstra Trigger
    private final Map<Integer, Integer> pointNodes; // Random Score

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.totalSquares = rows * cols; // 60
        this.links = new HashMap<>();
        this.starPositions = new HashSet<>();
        this.primePositions = new HashSet<>();
        this.pointNodes = new HashMap<>();

        initializeBoard();
    }

    private void initializeBoard() {
        // [PENTING] Urutan ini diubah agar Logic Prima berjalan duluan
        generatePrimePositions();   // 1. Cari Angka Prima dulu
        generateRandomLinks();      // 2. Pasang Tangga HANYA di Angka Prima
        generateStarPositions();    // 3. Pasang Bintang
        generateRandomPoints();     // 4. Pasang Poin
    }

    // --- SYARAT: TANGGA HANYA DI ANGKA PRIMA & JARAK PENDEK ---
    private void generateRandomLinks() {
        Random rand = new Random();

        // Kumpulkan semua kandidat posisi start yang merupakan Angka Prima
        // dan posisinya tidak terlalu dekat dengan finish (agar ada ruang untuk naik)
        List<Integer> validPrimes = new ArrayList<>();
        for (int p : primePositions) {
            if (p <= totalSquares - 10) {
                validPrimes.add(p);
            }
        }

        int count = 0;
        // Kita coba buat maksimal 5 tangga (atau sebanyak jumlah prima yang tersedia)
        while (count < 5 && !validPrimes.isEmpty()) {

            // Pilih satu angka prima secara acak
            int randomIndex = rand.nextInt(validPrimes.size());
            int from = validPrimes.get(randomIndex);

            // Tentukan tujuan (Jarak pendek: 5 - 15 langkah)
            int jumpDistance = rand.nextInt(11) + 5;
            int to = from + jumpDistance;

            // Validasi: Tujuan harus valid dan belum ada link di sana
            if (to < totalSquares && !links.containsKey(from) && !links.containsKey(to)) {
                links.put(from, new Link(from, to)); // Link terbentuk
                count++;

                // Hapus dari daftar agar tidak dipilih lagi (Satu prima max 1 tangga)
                validPrimes.remove(randomIndex);
            } else {
                // Jika gagal (misal tujuan overlap), coba lagi tanpa menghapus kandidat
                // (Atau hapus jika terlalu sulit, tapi biarkan loop berjalan)
            }
        }
    }

    // --- SYARAT 3: RANDOM SCORE ---
    private void generateRandomPoints() {
        Random rand = new Random();
        int[] values = {10, 20, 50, 100};
        int count = 0;
        while (count < 10) {
            int pos = rand.nextInt(totalSquares - 2) + 2;
            // Pastikan tidak menumpuk di tempat yang sudah ada link
            if (!links.containsKey(pos) && !pointNodes.containsKey(pos)) {
                pointNodes.put(pos, values[rand.nextInt(values.length)]);
                count++;
            }
        }
    }

    // --- DIJKSTRA ALGORITHM ---
    public List<Integer> findShortestPath(int start, int end) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();

        for (int i = 1; i <= totalSquares; i++) dist.put(i, Integer.MAX_VALUE);

        dist.put(start, 0);
        pq.offer(new int[]{start, 0});
        parent.put(start, null);

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int d = current[1];

            if (d > dist.get(u)) continue;
            if (u == end) return reconstructPath(parent, start, end);

            for (int step = 1; step <= 6; step++) {
                int v = u + step;
                if (v > totalSquares) continue;

                if (links.containsKey(v)) {
                    v = links.get(v).getTo();
                }

                if (dist.get(u) + 1 < dist.get(v)) {
                    dist.put(v, dist.get(u) + 1);
                    parent.put(v, u);
                    pq.offer(new int[]{v, dist.get(v)});
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Integer> reconstructPath(Map<Integer, Integer> parent, int start, int end) {
        List<Integer> path = new ArrayList<>();
        Integer curr = end;
        while (curr != null) {
            path.add(0, curr);
            curr = parent.get(curr);
        }
        return path;
    }

    private void generatePrimePositions() {
        for (int i = 2; i <= totalSquares; i++) {
            if (isPrimeNum(i)) primePositions.add(i);
        }
    }

    private boolean isPrimeNum(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }

    private void generateStarPositions() {
        for (int i = 7; i < totalSquares; i += 7) starPositions.add(i);
    }

    // --- GETTERS ---
    public boolean hasStar(int pos) { return starPositions.contains(pos); }
    public boolean isPrime(int pos) { return primePositions.contains(pos); }
    public Map<Integer, Link> getLinks() { return links; }
    public int getTotalSquares() { return totalSquares; }
    public Map<Integer, Integer> getPointsMap() { return pointNodes; }

    public int collectPoint(int pos) {
        Integer val = pointNodes.remove(pos);
        return val != null ? val : 0;
    }
}