    package org.example;
    import java.io.*;

    public class Task  {
        private String description;
        private boolean completed;

        public Task(String description) {
            this.description = description;
            this.completed = false;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void markCompleted(boolean b) {
            this.completed = b;
        }


        @Override
        public String toString() {
            return description + (completed ? " (Completed)" : "");
        }
    }