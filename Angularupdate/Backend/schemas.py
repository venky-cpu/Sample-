from pydantic import BaseModel
from typing import Optional, List

class Step(BaseModel):
    action: str
    selector: Optional[str] = None
    url: Optional[str] = None
    text: Optional[str] = None
    expected_result: Optional[str] = None

class Plan(BaseModel):
    steps: List[Step]
