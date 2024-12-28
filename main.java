import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

public class PronunciationAssessmentApp {

    // Replace with your Azure Speech Service key and region
    private static final String AZURE_SPEECH_KEY = "YOUR_API_KEY_HERE";
    private static final String AZURE_REGION = "YOUR_REGION_HERE";

    public static void main(String[] args) {
        try {
            // Setup Speech Configuration
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(AZURE_SPEECH_KEY, AZURE_REGION);

            // Set Pronunciation Assessment Configurations
            String referenceText = "Hello, how are you doing today?";
            PronunciationAssessmentConfig pronunciationConfig = new PronunciationAssessmentConfig(
                    referenceText,
                    PronunciationAssessmentGradingSystem.HundredMark,
                    PronunciationAssessmentGranularity.Phoneme
            );

            // Configure audio input (from microphone)
            AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();

            // Create recognizer
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            // Apply pronunciation assessment settings
            pronunciationConfig.applyTo(recognizer);

            System.out.println("Speak the following sentence: \"" + referenceText + "\"");

            // Start speech recognition
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            // Handle the result
            if (result.getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("Recognized: " + result.getText());

                // Extract pronunciation assessment result
                PronunciationAssessmentResult pronunciationResult =
                        PronunciationAssessmentResult.fromResult(result);

                System.out.println("Pronunciation Score: " + pronunciationResult.getPronunciationScore());
                System.out.println("Accuracy Score: " + pronunciationResult.getAccuracyScore());
                System.out.println("Fluency Score: " + pronunciationResult.getFluencyScore());
                System.out.println("Completeness Score: " + pronunciationResult.getCompletenessScore());
            } else {
                System.out.println("Speech not recognized. Reason: " + result.getReason());
            }

            // Close resources
            recognizer.close();
            audioConfig.close();
            speechConfig.close();
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
