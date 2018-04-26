package main

// Todo represents a todo
type Todo struct {
	ID      string `json:"id"`
	Text    string `json:"text"`
	Checked bool   `json:"checked"`
}

// TodoResponse contains an array of todos
type TodoResponse struct {
	Data []Todo `json:"data"`
}
