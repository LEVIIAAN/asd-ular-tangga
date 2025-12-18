import java.util.*;

public class LeaderboardManager {
    // Menyimpan skor sementara sesi ini
    private static Map<String, Integer> globalScoreMap = new HashMap<>();
    private static Map<String, Integer> globalWinMap = new HashMap<>();

    public static void addScore(String playerName, int points) {
        globalScoreMap.put(playerName, globalScoreMap.getOrDefault(playerName, 0) + points);
    }

    public static void addWin(String playerName) {
        globalWinMap.put(playerName, globalWinMap.getOrDefault(playerName, 0) + 1);
        globalScoreMap.putIfAbsent(playerName, 0);
    }

    public static int getScore(String playerName) {
        return globalScoreMap.getOrDefault(playerName, 0);
    }

    // --- REVISI: MENGGUNAKAN PRIORITY QUEUE SESUAI REQUEST ---
    public static List<Map.Entry<String, Integer>> getTop3Scores() {
        // 1. Buat PriorityQueue (Max-Heap) berdasarkan Skor Terbesar
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue() // Logic: Yang skornya besar di prioritas atas
        );

        // 2. Masukkan semua data dari Map ke PriorityQueue
        pq.addAll(globalScoreMap.entrySet());

        // 3. Ambil (Poll) 3 teratas dari Queue
        List<Map.Entry<String, Integer>> top3 = new ArrayList<>();
        int count = 0;

        while (!pq.isEmpty() && count < 3) {
            top3.add(pq.poll()); // Mengambil elemen prioritas tertinggi
            count++;
        }

        return top3;
    }
}