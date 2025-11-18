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

  getAllCourses(
    currentPage: number,
    pageSize: number,
    text?: string,
    options?: { simple?: boolean }
  ): Observable<any> {

    const simple = options?.simple ?? false;

    let url = `${this.baseUrl}/get?page=${currentPage}&size=${pageSize}`;
    if (text && text.trim().length > 0) {
      url += `&text=${encodeURIComponent(text)}`;
    }

    return this.http
      .get<ApiEnvelope<any>>(url)
      .pipe(
        map(res => this.normalize(res.result?.data, simple)),
        catchError(this.handleError)
      );
  }

  getLiveCourses(
    currentPage: number,
    pageSize: number,
    text?: string,
    options?: { simple?: boolean }
  ): Observable<any> {

    const simple = options?.simple ?? false;

    let url = `${this.baseUrl}/get/live?page=${currentPage}&size=${pageSize}`;
    if (text && text.trim().length > 0) {
      url += `&text=${encodeURIComponent(text)}`;
    }

    return this.http
      .get<ApiEnvelope<any>>(url)
      .pipe(
        map(res => this.normalize(res.result?.data, simple)),
        catchError(this.handleError)
      );
  }

  getCourseById(id: string): Observable<Course> {
    return this.http
      .get<ApiEnvelope<Course>>(`${this.baseUrl}/get/${id}`)
      .pipe(
        map(res => res.result?.data),
        catchError(this.handleError)
      );
  }

  createCourse(course: Course): Observable<Course> {
    return this.http
      .post<ApiEnvelope<Course>>(`${this.baseUrl}/add`, course)
      .pipe(
        map(res => res.result?.data),
        catchError(this.handleError)
      );
  }

  updateCourse(id: string, course: Course): Observable<Course> {
    return this.http
      .put<ApiEnvelope<Course>>(`${this.baseUrl}/update/${id}`, course)
      .pipe(
        map(res => res.result?.data),
        catchError(this.handleError)
      );
  }

  deleteCourse(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }

  private handleError(error: HttpErrorResponse) {
    console.error('HTTP Error:', error);
    return throwError(() => error);
  }
}
