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

  constructor(private http: HttpClient) {}

  private normalize(data: any, simple: boolean) {


    if (Array.isArray(data)) {
      if (simple) return data;

      return {
        content: data,
        totalElements: data.length,
        totalPages: 1,
        size: data.length,
        number: 0
      };
    }

    if (data && data.content) {
      return data;
    }

    return {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 0,
      number: 0
    };
  }

   listCourses(body: any): Observable<any> {
    return this.http.post<ApiEnvelope<any>>(`${this.baseUrl}/list`, body)
      .pipe(
        map(res => res.result?.data),
        catchError(this.handleError)
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