import os
from openai import OpenAI

class LLMDriver:
    def __init__(self):
        self.client = OpenAI(
            api_key=os.getenv('MODEL_API_KEY'),
            base_url=os.getenv('MODEL_BASE_URL', 'https://api.openai.com/v1')
        )
        self.model = os.getenv('MODEL_NAME', 'mistral-large-latest')

    def chat(self, system_prompt: str, user_prompt: str, temperature: float = 0.2):
        messages = [
            {'role': 'system', 'content': system_prompt},
            {'role': 'user', 'content': user_prompt},
        ]
        resp = self.client.chat.completions.create(
            model=self.model,
            messages=messages,
            temperature=temperature
        )
        try:
            return resp.choices[0].message.content.strip()
        except Exception:
            return str(resp)
