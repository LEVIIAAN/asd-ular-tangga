import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {
    private Clip bgmClip;

    // Status Mute
    private boolean isSfxMuted = false;
    private boolean isBgmMuted = false;

    // Volume (Range 0.0f sampai 1.0f)
    private float bgmVolume = 0.5f;
    private float sfxVolume = 1.0f;

    // Cache nilai dB terakhir untuk mencegah update berlebihan (Zipper Noise)
    private float lastBgmDb = -999f;

    // --- PENGATURAN VOLUME ---

    public void setBgmVolume(float volume) {
        // Clamp 0.0 - 1.0
        this.bgmVolume = Math.max(0.0f, Math.min(volume, 1.0f));

        if (bgmClip != null && bgmClip.isOpen()) {
            updateClipGain(bgmClip, this.bgmVolume, true); // True = ini BGM
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(volume, 1.0f));
    }

    public float getBgmVolume() { return bgmVolume; }
    public float getSfxVolume() { return sfxVolume; }

    // Helper: Mengubah nilai 0.0-1.0 menjadi Decibels dengan aman
    private void updateClipGain(Clip clip, float volume, boolean isBgm) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // 1. Rumus Konversi Logaritma
                // Jika volume sangat kecil, set ke minimum dB agar hening total
                float db = (volume <= 0.05f) ? -80.0f : (float) (20f * Math.log10(volume));

                // 2. [FIX NOISE] Batasi Maksimum di 0.0 dB (Jangan diamplifikasi!)
                // Sebelumnya: Math.min(db, gainControl.getMaximum()); -> Ini bisa +6dB (Penyebab Distorsi)
                db = Math.min(db, 0.0f);

                // Batasi Minimum sesuai hardware
                db = Math.max(db, gainControl.getMinimum());

                // 3. [FIX ZIPPER NOISE] Cek apakah perubahan cukup signifikan?
                // Jika perubahan < 0.1 dB, abaikan agar driver sound tidak "kaget"
                if (isBgm) {
                    if (Math.abs(db - lastBgmDb) < 0.1f) return;
                    lastBgmDb = db;
                }

                gainControl.setValue(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- SFX ---
    public void setSfxMuted(boolean muted) { this.isSfxMuted = muted; }
    public boolean isSfxMuted() { return isSfxMuted; }

    public void playSfx(String filename) {
        if (isSfxMuted) return;

        new Thread(() -> {
            try {
                String path = filename.startsWith("/") ? filename : "/assets/" + filename;
                URL url = getClass().getResource(path);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);

                    // Terapkan volume SFX (false = bukan bgm, tidak perlu cache)
                    updateClipGain(clip, sfxVolume, false);

                    clip.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // --- BGM ---
    public void playBgm(String filename) {
        isBgmMuted = false;
        stopBgm();
        new Thread(() -> {
            try {
                String path = filename.startsWith("/") ? filename : "/assets/" + filename;
                URL url = getClass().getResource(path);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    bgmClip = AudioSystem.getClip();
                    bgmClip.open(audioIn);

                    // Terapkan volume BGM
                    updateClipGain(bgmClip, bgmVolume, true);

                    bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                    bgmClip.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopBgm() {
        isBgmMuted = true;
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    public boolean isBgmMuted() { return isBgmMuted; }
}