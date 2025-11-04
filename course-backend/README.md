# Course Frontend

A modern Angular frontend application for managing courses and units, built with Angular Material and TypeScript.

## Features

- **Course Management**: View, create, and manage courses with filtering and search capabilities
- **Unit Management**: Create and manage course units
- **Responsive Design**: Built with Angular Material for a modern, responsive UI
- **Advanced Filtering**: Filter courses by board, medium, grade, and subject
- **Search Functionality**: Search courses by name and description

## Tech Stack

- Angular 18
- Angular Material
- TypeScript
- SCSS
- RxJS

## Getting Started

### Prerequisites

- Node.js (v18 or higher)
- npm or yarn
- Angular CLI

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm start
```

3. Open your browser and navigate to `http://localhost:4200`

### Backend Integration

This frontend is designed to work with a Spring Boot backend. Make sure your backend is running on `http://localhost:8089` with the following endpoints:

- `GET /api/courses` - Get all courses
- `POST /api/courses` - Create a new course
- `POST /api/units` - Create a new unit

## Project Structure

```
src/app/
├── components/
│   ├── course-list/     # Course listing with filters
│   ├── course-form/     # Course creation form
│   └── unit-form/       # Unit creation form
├── services/
│   ├── course.service.ts
│   └── unit.service.ts
├── models/
│   ├── course.model.ts
│   └── unit.model.ts
├── pipes/
│   └── join-list.pipe.ts
└── app-routing.module.ts
```

## Development

### Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm test` - Run unit tests

### Code Style

This project follows Angular best practices:
- Standalone components
- Reactive forms
- TypeScript strict mode
- SCSS for styling
- Angular Material for UI components