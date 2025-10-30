import os
import time
from pathlib import Path
from playwright.sync_api import sync_playwright

SCREENSHOT_DIR = Path(os.getcwd()) / 'screenshots'
SCREENSHOT_DIR.mkdir(exist_ok=True)

class PlaywrightExecutor:
    def __init__(self, headless=True):
        self.status = {'running': False, 'last_step': None, 'logs': []}
        self.headless = headless if isinstance(headless, bool) else (os.getenv('PLAYWRIGHT_HEADLESS','true').lower() in ('1','true','yes'))

    def reconnaissance(self, start_url: str, max_elements=200):
        '''Visit start_url and return a small DOM map (buttons, inputs, links)'''
        dom_map = []
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=self.headless)
            context = browser.new_context()
            page = context.new_page()
            page.goto(start_url, wait_until='load', timeout=15000)
            # evaluate a JS snippet to collect simple element info
            dom_map = page.evaluate("""() => {
                const nodes = Array.from(document.querySelectorAll('button,a,input,select,textarea'));
                function getXPath(element){ if (element.id) return 'id("'+element.id+'")'; const parts = []; while(element && element.nodeType===1){ let idx=1; let sib=element.previousSibling; while(sib){ if(sib.nodeType===1 && sib.nodeName===element.nodeName) idx++; sib = sib.previousSibling;} parts.unshift(element.nodeName.toLowerCase()+'['+idx+']'); element = element.parentNode;} return '/'+parts.join('/'); }
                return nodes.slice(0, max_elements).map(n=>({tag:n.tagName, text:n.innerText||n.value, id:n.id, classes:n.className, xpath:getXPath(n)}));
            })""")
            context.close(); browser.close()
        return dom_map

    def execute_plan(self, plan_steps):
        self.status.update({'running': True, 'logs': []})
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=self.headless)
            context = browser.new_context()
            page = context.new_page()
            step_idx = 0
            for step in plan_steps:
                step_idx += 1
                try:
                    action = step.get('action')
                    if action == 'goto':
                        url = step.get('url')
                        page.goto(url, wait_until='load', timeout=15000)
                    elif action == 'click':
                        sel = step.get('selector')
                        if not page.query_selector(sel):
                            raise Exception(f'Selector not found: {sel}')
                        page.click(sel)
                    elif action == 'type':
                        sel = step.get('selector'); txt = step.get('text','')
                        if not page.query_selector(sel):
                            raise Exception(f'Selector not found: {sel}')
                        page.fill(sel, txt)
                    elif action == 'wait':
                        time.sleep(step.get('seconds',1))
                    elif action in ('assert_text','assert'):
                        sel = step.get('selector'); expected = step.get('expected') or step.get('expected_result')
                        if not page.query_selector(sel):
                            raise Exception(f'Selector not found: {sel}')
                        text = page.text_content(sel) or ''
                        if expected not in text:
                            raise Exception(f"Assertion failed: expected '{expected}' in '{text}'")
                    else:
                        # unknown action -> skip
                        pass
                    shot = SCREENSHOT_DIR / f'step_{step_idx}.png'
                    page.screenshot(path=str(shot), full_page=False)
                    self.status['last_step'] = step
                    self.status['logs'].append({'step': step, 'ok': True, 'screenshot': str(shot)})
                except Exception as e:
                    try:
                        err_shot = SCREENSHOT_DIR / f'step_{step_idx}_error.png'
                        page.screenshot(path=str(err_shot), full_page=False)
                    except Exception:
                        err_shot = None
                    log = {'step': step, 'ok': False, 'error': str(e), 'screenshot': str(err_shot) if err_shot else None}
                    self.status['logs'].append(log)
            context.close(); browser.close()
        self.status.update({'running': False})

    def get_status(self):
        return self.status
