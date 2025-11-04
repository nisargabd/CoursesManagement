import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { environment } from '../../environments/environment';

interface PaginatedResponse<T> {
  content: T[];
  pageable: any;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface ApiEnvelope<T> {
  id: string;
  ver: string;
  ts: string;
  responseCode: string;
  params: any;
  result: {
    message: string;
    data: T;
  };
}

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private baseUrl = environment.baseUrl;

  constructor(private http: HttpClient) { }

  getAllCourses(page: number = 0, size: number = 10): Observable<PaginatedResponse<Course>> {
    return this.http.get<PaginatedResponse<Course>>(`${this.baseUrl}/courses/get?page=${page}&size=${size}`);
  }

  getAllCoursesSimple(): Observable<Course[]> {
    return this.http.get<PaginatedResponse<Course>>(`${this.baseUrl}/courses/get?page=0&size=1000`)
      .pipe(
        map(response => response.content || [])
      );
  }

  getCourseById(id: string): Observable<Course> {
    return this.http.get<ApiEnvelope<Course>>(`${this.baseUrl}/courses/get/${id}`)
      .pipe(
        map(response => {
          console.log('Raw API response:', response);
          console.log('Response result:', response.result);
          console.log('Response data:', response.result.data);
          return response.result.data;
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('HTTP Error:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          console.error('Error body:', error.error);
          return throwError(() => error);
        })
      );
  }

  createCourse(course: Course): Observable<Course> {
    return this.http.post<ApiEnvelope<Course>>(`${this.baseUrl}/courses/add`, course)
      .pipe(
        map(response => response.result.data)
      );
  }

  updateCourse(id: string, course: Course): Observable<Course> {
    return this.http.put<ApiEnvelope<Course>>(`${this.baseUrl}/courses/update/${id}`, course)
      .pipe(
        map(response => response.result.data)
      );
  }

  deleteCourse(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/courses/delete/${id}`);
  }
}
