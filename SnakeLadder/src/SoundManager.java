import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {
    private Clip bgmClip;
    // Tambahan: status mute untuk SFX
    private boolean isSfxMuted = false;

    public void setSfxMuted(boolean muted) {
        this.isSfxMuted = muted;
    }

    public boolean isSfxMuted() {
        return isSfxMuted;
    }

    public void playSfx(String filename) {
        // Cek dulu, kalau dimute, jangan dimainkan
        if (isSfxMuted) return;

        new Thread(() -> {
            try {
                String path = filename.startsWith("/") ? filename : "/assets/" + filename;
                URL url = getClass().getResource(path);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void playBgm(String filename) {
        // Kalau sudah ada yang main, stop dulu
        stopBgm();
        new Thread(() -> {
            try {
                String path = filename.startsWith("/") ? filename : "/assets/" + filename;
                URL url = getClass().getResource(path);
                if (url != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    bgmClip = AudioSystem.getClip();
                    bgmClip.open(audioIn);
                    bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                    bgmClip.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopBgm() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    public boolean isBgmMuted() {
        return bgmClip == null || !bgmClip.isRunning();
    }
}