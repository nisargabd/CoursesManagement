import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { environment } from '../../environments/environment';

interface PaginatedResponse<T> {
  content: T[];
  pageable?: any;
  totalElements?: number;
  totalPages?: number;
  size?: number;
  number?: number;
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

  constructor(private http: HttpClient) {}

  /** ✅ Handles list or paginated responses automatically */
  private normalizePaginatedResponse(response: any): PaginatedResponse<Course> {
    const data = response.result?.data;
    if (Array.isArray(data)) {
      // Backend sent plain array
      return {
        content: data,
        totalElements: data.length,
        totalPages: 1,
        size: data.length,
        number: 0
      };
    }
    // Backend sent proper page object
    return data;
  }

  /** ✅ Admin: all courses paginated */
  getAllCourses(page: number = 0, size: number = 10): Observable<PaginatedResponse<Course>> {
    return this.http
      .get<ApiEnvelope<any>>(`${this.baseUrl}/courses/get?page=${page}&size=${size}`)
      .pipe(
        map(response => this.normalizePaginatedResponse(response)),
        catchError(this.handleError)
      );
  }

  /** ✅ Admin: all courses (flat list) */
  getAllCoursesSimple(): Observable<Course[]> {
    return this.http
      .get<ApiEnvelope<any>>(`${this.baseUrl}/courses/get?page=0&size=1000`)
      .pipe(
        map(response => {
          const data = response.result?.data;
          return Array.isArray(data) ? data : data?.content || [];
        }),
        catchError(this.handleError)
      );
  }

  /** ✅ User: live courses paginated */
  getLiveCourses(page: number = 0, size: number = 10): Observable<PaginatedResponse<Course>> {
    return this.http
      .get<ApiEnvelope<any>>(`${this.baseUrl}/courses/public?page=${page}&size=${size}`)
      .pipe(
        map(response => this.normalizePaginatedResponse(response)),
        catchError(this.handleError)
      );
  }

  /** ✅ User: live courses (flat list) */
  getLiveCoursesSimple(): Observable<Course[]> {
    return this.http
      .get<ApiEnvelope<any>>(`${this.baseUrl}/courses/public?page=0&size=1000`)
      .pipe(
        map(response => {
          const data = response.result?.data;
          return Array.isArray(data) ? data : data?.content || [];
        }),
        catchError(this.handleError)
      );
  }

  /** ✅ Get by ID */
  getCourseById(id: string): Observable<Course> {
    return this.http
      .get<ApiEnvelope<Course>>(`${this.baseUrl}/courses/get/${id}`)
      .pipe(map(response => response.result.data), catchError(this.handleError));
  }

  createCourse(course: Course): Observable<Course> {
    return this.http
      .post<ApiEnvelope<Course>>(`${this.baseUrl}/courses/add`, course)
      .pipe(map(res => res.result.data), catchError(this.handleError));
  }

  updateCourse(id: string, course: Course): Observable<Course> {
    return this.http
      .put<ApiEnvelope<Course>>(`${this.baseUrl}/courses/update/${id}`, course)
      .pipe(map(res => res.result.data), catchError(this.handleError));
  }

  deleteCourse(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/courses/delete/${id}`);
  }

  private handleError(error: HttpErrorResponse) {
    console.error('HTTP Error:', error);
    return throwError(() => error);
  }
}
 