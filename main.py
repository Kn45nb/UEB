from openai import OpenAI
from fastapi import FastAPI, HTTPException, UploadFile, Form
from starlette.responses import JSONResponse

client = OpenAI(api_key="")

app = FastAPI()

@app.post("/transcribe")
async def transcribe_audio(file: UploadFile):
    try:
        # Đọc file audio upload
        audio_data = await file.read()

        # Gửi yêu cầu đến API OpenAI
        transcription = client.Audio.transcriptions.create(
            file=audio_data,
            model="whisper-1",  # Model speech-to-text của OpenAI
            response_format="text"  # Trả về văn bản
        )

        return {"transcription": transcription}
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})
