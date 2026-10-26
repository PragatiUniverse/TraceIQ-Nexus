"""Live Investigation Challenge Engine for Feature 7."""
import datetime
from typing import List, Dict, Any, Optional
from traceiq.data_loader import load_case_data
from traceiq.models import get_db_connection

class ChallengeEngine:
    def __init__(self):
        pass

    def get_challenge_status(self) -> Dict[str, Any]:
        case = load_case_data()
        tasks = case.get("challenge_tasks", [])
        
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT task_id, completed, score, completed_at, user_notes FROM challenge_progress")
        rows = {r["task_id"]: dict(r) for r in cur.fetchall()}
        conn.close()
        
        total_possible_score = sum(t["points"] for t in tasks)
        current_score = 0
        task_list = []
        
        for t in tasks:
            tid = t["id"]
            progress = rows.get(tid, {})
            is_completed = bool(progress.get("completed", 0))
            awarded = t["points"] if is_completed else 0
            if is_completed:
                current_score += awarded
                
            task_list.append({
                "id": tid,
                "title": t["title"],
                "description": t["description"],
                "points": t["points"],
                "verification_key": t["verification_key"],
                "hint": t.get("hint", ""),
                "is_completed": is_completed,
                "awarded_score": awarded,
                "completed_at": progress.get("completed_at"),
                "user_notes": progress.get("user_notes", "")
            })
            
        return {
            "challenge_name": "The Missing Research Files — Investigation Challenge",
            "case_id": case.get("case_info", {}).get("case_id"),
            "total_tasks": len(tasks),
            "completed_tasks": sum(1 for t in task_list if t["is_completed"]),
            "current_score": current_score,
            "total_possible_score": total_possible_score,
            "score_percentage": round((current_score / total_possible_score) * 100, 1) if total_possible_score > 0 else 0,
            "tasks": task_list,
            "scoring_disclaimer": "Score reflects completion of scripted educational tasks, not investigator competence or legal certainty."
        }

    def complete_task(self, task_id: str, user_notes: str = "") -> Dict[str, Any]:
        case = load_case_data()
        tasks = {t["id"]: t for t in case.get("challenge_tasks", [])}
        if task_id not in tasks:
            raise ValueError(f"Task ID '{task_id}' not found.")
            
        task = tasks[task_id]
        points = task["points"]
        now = datetime.datetime.now(datetime.timezone.utc).isoformat()
        
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("""
        INSERT INTO challenge_progress (task_id, completed, score, completed_at, user_notes)
        VALUES (?, 1, ?, ?, ?)
        ON CONFLICT(task_id) DO UPDATE SET
            completed=1,
            score=excluded.score,
            completed_at=excluded.completed_at,
            user_notes=excluded.user_notes
        """, (task_id, points, now, user_notes))
        conn.commit()
        conn.close()
        
        return self.get_challenge_status()

    def reset_challenge(self) -> Dict[str, Any]:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("DELETE FROM challenge_progress")
        conn.commit()
        conn.close()
        return self.get_challenge_status()
