import java.util.*;

public class GameBoard {
    private final int rows, cols;
    private final int totalSquares;
    private final Map<Integer, Link> links;
    private final Set<Integer> starPositions;     // Magic Bubble
    private final Set<Integer> primePositions;    // Untuk Syarat Naik Tangga & Dijkstra
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
        generatePrimePositions();   // 1. Generate Prima dulu
        generateRandomLinks();      // 2. Pasang TANGGA SAJA
        generateStarPositions();    // 3. Pasang Bintang
        generateRandomPoints();     // 4. Pasang Poin
    }

    // [REVISI] HANYA MEMBUAT TANGGA (NAIK) - TIDAK ADA ULAR
    private void generateRandomLinks() {
        Random rand = new Random();
        int count = 0;

        // Loop sampai terbentuk 5 Tangga
        while (count < 5) {
            // Pilih posisi start acak (2 sampai 50 agar cukup ruang untuk naik)
            int from = rand.nextInt(totalSquares - 15) + 2;

            // Tentukan jarak lompat (5 - 20 langkah)
            int jump = rand.nextInt(16) + 5;

            // [PERUBAHAN DISINI] Selalu Menjumlahkan (NAIK)
            int to = from + jump;

            // Validasi:
            // 1. Tujuan harus di dalam papan (< totalSquares)
            // 2. Tidak boleh overlap dengan start/end link lain
            if (to < totalSquares && !links.containsKey(from) && !links.containsKey(to)) {

                links.put(from, new Link(from, to));
                count++;
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
                    // Logic Dijkstra menganggap Tangga sebagai jalan pintas
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