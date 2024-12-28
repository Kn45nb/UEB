import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;
import java.io.FileInputStream;

public class GoogleSpeechMP3 {

    private static final String REFERENCE_TEXT = "Hello, how are you doing today?";

    public static void main(String[] args) {
        try {
            // File gốc MP3
            String mp3FilePath = "Recording.mp3";
            // File WAV sau khi chuyển đổi
            String wavFilePath = "Recording.wav";

            // Chuyển đổi MP3 sang WAV
            convertMp3ToWav(mp3FilePath, wavFilePath);

            // Gửi file WAV lên Google Speech-to-Text API
            String transcript = transcribeAudio(wavFilePath);

            // Hiển thị kết quả
            System.out.println("Reference Text: " + REFERENCE_TEXT);
            System.out.println("Transcript: " + transcript);
            evaluatePronunciation(REFERENCE_TEXT, transcript);

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm chuyển đổi MP3 sang WAV
    private static void convertMp3ToWav(String mp3FilePath, String wavFilePath) throws Exception {
        File source = new File(mp3FilePath);
        File target = new File(wavFilePath);

        // Cài đặt thuộc tính âm thanh WAV
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le"); // Định dạng LINEAR16
        audio.setBitRate(16000); // Tần số bit
        audio.setChannels(1); // Mono
        audio.setSamplingRate(16000); // Tần số mẫu 16kHz

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        encoder.encode(source, target, attrs);

        System.out.println("MP3 file converted to WAV: " + wavFilePath);
    }

    // Hàm gửi file WAV lên Google Speech-to-Text API
    private static String transcribeAudio(String filePath) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Load audio file
            ByteString audioBytes = ByteString.readFrom(new FileInputStream(filePath));

            // Configure recognition settings
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Perform speech recognition
            RecognizeResponse response = speechClient.recognize(config, audio);
            for (SpeechRecognitionResult result : response.getResultsList()) {
                return result.getAlternatives(0).getTranscript();
            }
        }
        return "";
    }

    // Đánh giá độ chính xác phát âm
    private static void evaluatePronunciation(String reference, String transcript) {
        // So sánh mức độ tương đồng giữa văn bản tham chiếu và kết quả nhận dạng
        int correctWords = 0;
        String[] referenceWords = reference.split(" ");
        String[] transcriptWords = transcript.split(" ");

        for (int i = 0; i < Math.min(referenceWords.length, transcriptWords.length); i++) {
            if (referenceWords[i].equalsIgnoreCase(transcriptWords[i])) {
                correctWords++;
            }
        }

        double accuracy = (double) correctWords / referenceWords.length * 100;
        System.out.println("Pronunciation Accuracy: " + accuracy + "%");
    }
}
