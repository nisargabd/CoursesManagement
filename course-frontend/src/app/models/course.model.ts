import { Unit } from './unit.model';

export interface Course {
  id?: string;
  name: string;
  description: string;
  board: string;
  medium: string[];
  grade: string[];
  subject: string[];
  units?: Unit[];
}

export interface CourseFilter {
  board?: string;
  medium?: string;
  grade?: string;
  subject?: string;
  search?: string;
}
