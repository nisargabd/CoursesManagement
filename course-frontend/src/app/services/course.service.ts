import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { environment } from '../../environments/environment';

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface ApiEnvelope<T> {
  result: {
    data: T;
  };
}

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  private readonly baseUrl = `${environment.baseUrl}/courses`;

  constructor(private http: HttpClient) { }

  listCourses(body: any): Observable<any> {
    console.log('[CourseService] Calling /list with body:', body);
    return this.http.post<ApiEnvelope<any>>(`${this.baseUrl}/list`, body)
      .pipe(
        map(res => {
          console.log('[CourseService] Raw API response:', res);
          console.log('[CourseService] res.result:', res.result);
          console.log('[CourseService] res.result?.data:', res.result?.data);
          const data = res.result?.data;
          console.log('[CourseService] Extracted data:', data);
          return data;
        }),
        catchError(err => {
          console.error('[CourseService] HTTP Error:', err);
          return this.handleError(err);
        })
      );
  }

  getCourseById(id: string): Observable<Course> {
    return this.http
      .get<ApiEnvelope<Course>>(`${this.baseUrl}/get/${id}`)
      .pipe(map(res => res.result?.data));
  }

  createCourse(course: Course): Observable<Course> {
    return this.http
      .post<ApiEnvelope<Course>>(`${this.baseUrl}/add`, course)
      .pipe(map(res => res.result?.data));
  }

  updateCourse(id: string, course: Course): Observable<Course> {
    return this.http
      .put<ApiEnvelope<Course>>(`${this.baseUrl}/update/${id}`, course)
      .pipe(map(res => res.result?.data));
  }

  deleteCourse(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }

  private handleError(error: any) {
    console.error('HTTP Error:', error);
    return throwError(() => error);
  }
}