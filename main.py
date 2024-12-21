<<<<<<< HEAD
from openai import OpenAI
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

client = OpenAI(api_key="")

app = FastAPI()

class ChatRequest(BaseModel):
    message: str

@app.post("/chat")
def post_chat_response(request: ChatRequest):
    try:
        completion = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": "Tôi là UserUser."},
                {"role": "user", "content": request.message}
            ]
        )
        
        response_message = completion.choices[0].message.content

        return {"response": response_message}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
=======
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
def root():
    return {"message": "Hello World"}
>>>>>>> parent of d052459 (test)
