package com.xxs.project.djangocalendar;

import java.io.Serializable;

public class EventModal implements Serializable {

    private String model;
    private int pk;
    private FieldsBean fields;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public FieldsBean getFields() {
        return fields;
    }

    public void setFields(FieldsBean fields) {
        this.fields = fields;
    }

    public static class FieldsBean {

        private String event_title;
        private String event_category;
        private String event_text;
        private String due_date;
        private String expected_date;
        private boolean is_finished;

        public String getEvent_title() {
            return event_title;
        }

        public void setEvent_title(String event_title) {
            this.event_title = event_title;
        }

        public String getEvent_category() {
            return event_category;
        }

        public void setEvent_category(String event_category) {
            this.event_category = event_category;
        }

        public String getEvent_text() {
            return event_text;
        }

        public void setEvent_text(String event_text) {
            this.event_text = event_text;
        }

        public String getDue_date() {
            return due_date;
        }

        public void setDue_date(String due_date) {
            this.due_date = due_date;
        }

        public String getExpected_date() {
            return expected_date;
        }

        public void setExpected_date(String expected_date) {
            this.expected_date = expected_date;
        }

        public boolean isIs_finished() {
            return is_finished;
        }

        public void setIs_finished(boolean is_finished) {
            this.is_finished = is_finished;
        }
    }
}
