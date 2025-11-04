import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Unit } from '../models/unit.model';
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
export class UnitService {
  private baseUrl = environment.baseUrl;

  constructor(private http: HttpClient) { }

  getAllUnits(): Observable<Unit[]> {
    return this.http.get<ApiEnvelope<Unit[]>>(`${this.baseUrl}/units`)
      .pipe(
        map(response => {
          console.log('Raw Units API response:', response);
          console.log('Units result:', response.result);
          console.log('Units data:', response.result.data);
          return response.result.data || [];
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Units HTTP Error:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          console.error('Error body:', error.error);
          return throwError(() => error);
        })
      );
  }

  getUnitsByCourse(courseId: string): Observable<Unit[]> {
    return this.getAllUnits().pipe(
      map(units => (units || []).filter(u => u.courseId === courseId))
    );
  }

  getUnitById(id: string): Observable<Unit> {
    return this.http.get<ApiEnvelope<Unit>>(`${this.baseUrl}/units/get/${id}`)
      .pipe(
        map(response => response.result.data)
      );
  }

  createUnit(unit: Unit): Observable<Unit> {
    return this.http.post<ApiEnvelope<Unit>>(`${this.baseUrl}/units/add`, unit)
      .pipe(
        map(response => response.result.data)
      );
  }

  updateUnit(id: string, unit: Unit): Observable<Unit> {
    return this.http.put<ApiEnvelope<Unit>>(`${this.baseUrl}/units/update/${id}`, unit)
      .pipe(
        map(response => response.result.data)
      );
  }

  deleteUnit(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/units/delete/${id}`);
  }
}