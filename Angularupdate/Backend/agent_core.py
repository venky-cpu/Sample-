import json
from agent.llm_driver import LLMDriver
from agent.playwright_executor import PlaywrightExecutor

class QATestAgent:
    def __init__(self, context_store=None):
        self.llm = LLMDriver()
        self.executor = PlaywrightExecutor()
        self.context_store = context_store
        self._status = {'running': False, 'logs': []}

    def _plan_from_context(self, dom_map=None):
        # Retrieve a brief context summary from context store
        ctx = ''
        if self.context_store:
            try:
                res = self.context_store.query('list important test cases for this application')
                docs = res.get('documents', [[]])[0]
                ctx = '\n'.join([d for d in docs if d])
            except Exception:
                ctx = ''

        system = ('You are a QA test planner. Output ONLY a valid JSON array of step objects. ' 
                  'Schema: [{action: "goto"|"click"|"type"|"assert_text"|"wait"|"note", selector?:string, url?:string, text?:string, expected_result?:string}].')
        user = f'Project context:\n{ctx}\n\nDOM_MAP:\n{json.dumps(dom_map) if dom_map else ""}\n\nProduce 6-12 atomic test steps as JSON where each step has action, selector/url/text/expected_result where applicable.'
        resp = self.llm.chat(system, user)
        try:
            plan = json.loads(resp)
        except Exception:
            start = resp.find('[')
            end = resp.rfind(']')
            if start != -1 and end != -1:
                try:
                    plan = json.loads(resp[start:end+1])
                except Exception:
                    plan = [{'action':'note','text':resp}]
            else:
                plan = [{'action':'note','text':resp}]
        return plan

    def run_test_cycle(self, start_url: str = 'http://localhost:4200'):
        self._status = {'running': True, 'logs': []}
        # 1. reconnaissance
        try:
            dom_map = self.executor.reconnaissance(start_url)
        except Exception:
            dom_map = None
        # 2. planning
        plan = self._plan_from_context(dom_map=dom_map)
        if isinstance(plan, dict):
            plan = [plan]
        # 3. execute
        self.executor.execute_plan(plan)
        self._status = self.executor.get_status()

    def get_status(self):
        s = {'running': self._status.get('running', False)}
        s.update(self.executor.get_status())
        return s
