import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CourseService } from '../../services/course.service';
import { FilterOptionsService, FilterOptions } from '../../services/filter-options.service';
import { Course, CourseFilter } from '../../models/course.model';
import { JoinListPipe } from '../../pipes/join-list.pipe';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    JoinListPipe,
    ConfirmDialogComponent
  ],
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.scss']
})
export class CourseListComponent implements OnInit {
  courses: Course[] = [];
  filteredCourses: Course[] = [];
  searchTerm: string = '';
  filters: CourseFilter = {};
  
  // Pagination properties
  totalElements: number = 0;
  pageSize: number = 5;
  currentPage: number = 0;
  isLoading: boolean = false;
  
  // Search mode - if true, load all courses for client-side filtering
  searchMode: boolean = false;

  // Filter options - will be loaded from backend
  boardOptions: string[] = [];
  mediumOptions: string[] = [];
  gradeOptions: string[] = [];
  subjectOptions: string[] = [];
  filterOptionsLoading: boolean = true;

  constructor(
    private courseService: CourseService,
    private filterOptionsService: FilterOptionsService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadFilterOptions();
    this.loadCourses();
  }

  loadFilterOptions(): void {
    this.filterOptionsLoading = true;
    this.filterOptionsService.getFilterOptions().subscribe({
      next: (options: FilterOptions) => {
        this.boardOptions = options.boards;
        this.mediumOptions = options.mediums;
        this.gradeOptions = options.grades;
        this.subjectOptions = options.subjects;
        this.filterOptionsLoading = false;
        console.log('Filter options loaded:', options);
      },
      error: (error) => {
        console.error('Error loading filter options:', error);
        // Fallback to hardcoded values if API fails
        this.boardOptions = ['State', 'CBSE', 'ICSE',"XYZ"];
        this.mediumOptions = ['English', 'Kannada', 'Hindi', 'Telugu'];
        this.gradeOptions = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'];
        this.subjectOptions = [
          'English', 'Kannada', 'Hindi', 'Maths', 'Science', 'Social', 
          'Physics', 'Chemistry', 'Biology', 'History', 'Geography', 'Civics', 'Computer'
        ];
        this.filterOptionsLoading = false;
        this.snackBar.open('Using fallback filter options', 'Close', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
      }
    });
  }
  onBoardChange(): void {
  if (this.filters.board) {
    const req = { board: this.filters.board };
    this.filterOptionsService.getMediumsByBoard(req).subscribe({
      next: (mediums) => {
        this.mediumOptions = mediums;
        this.filters.medium = '';
        this.gradeOptions = [];
        this.subjectOptions = [];
      },
      error: (err) => console.error('Error fetching mediums:', err)
    });
  }
}

onMediumChange(): void {
  if (this.filters.board && this.filters.medium) {
    const req = {
      board: this.filters.board,
      medium: [this.filters.medium]
    };
    this.filterOptionsService.getGradesByBoardAndMedium(req).subscribe({
      next: (grades) => {
        this.gradeOptions = grades;
        this.filters.grade = '';
        this.subjectOptions = [];
      },
      error: (err) => console.error('Error fetching grades:', err)
    });
  }
}

onGradeChange(): void {
  if (this.filters.board && this.filters.medium && this.filters.grade) {
    const req = {
      board: this.filters.board,
      medium: [this.filters.medium],
      grade: [this.filters.grade]
    };
    this.filterOptionsService.getSubjectsByBoardMediumAndGrade(req).subscribe({
      next: (subjects) => {
        this.subjectOptions = subjects;
      },
      error: (err) => console.error('Error fetching subjects:', err)
    });
  }
}


  loadCourses(): void {
    this.isLoading = true;
    
    if (this.searchMode || this.searchTerm || this.hasActiveFilters()) {
      // Load all courses for client-side filtering
      this.courseService.getAllCoursesSimple().subscribe({
        next: (courses) => {
          this.courses = Array.isArray(courses) ? courses : [];
          this.applyFilters();
          // In search/filter mode, total should reflect filtered items count
          this.totalElements = this.filteredCourses.length;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading courses:', error);
          this.courses = [];
          this.filteredCourses = [];
          this.totalElements = 0;
          this.isLoading = false;
        }
      });
    } else {
      // Load paginated courses
      this.courseService.getAllCourses(this.currentPage, this.pageSize).subscribe({
        next: (response) => {
          this.courses = Array.isArray(response.content) ? response.content : [];
          this.totalElements = response.totalElements || 0;
          this.filteredCourses = [...this.courses]; // No filtering in pagination mode
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading courses:', error);
          this.courses = [];
          this.filteredCourses = [];
          this.totalElements = 0;
          this.isLoading = false;
        }
      });
    }
  }

  hasActiveFilters(): boolean {
    return !!(this.filters.board || this.filters.medium || this.filters.grade || this.filters.subject);
  }

  applyFilters(): void {
    // Ensure courses is always an array
    if (!Array.isArray(this.courses)) {
      this.courses = [];
    }
    
    this.filteredCourses = this.courses.filter(course => {
      // Search filter
      if (this.searchTerm) {
        const searchLower = this.searchTerm.toLowerCase();
        const matchesSearch = 
          course.name.toLowerCase().includes(searchLower) ||
          course.description.toLowerCase().includes(searchLower);
        if (!matchesSearch) return false;
      }

      // Board filter
      if (this.filters.board && course.board !== this.filters.board) {
        return false;
      }

      // Medium filter
      if (this.filters.medium && !course.medium.includes(this.filters.medium)) {
        return false;
      }

      // Grade filter
      if (this.filters.grade && !course.grade.includes(this.filters.grade)) {
        return false;
      }

      // Subject filter
      if (this.filters.subject && !course.subject.includes(this.filters.subject)) {
        return false;
      }

      return true;
    });

    // Keep totals in sync and clamp page index after filtering
    const total = this.filteredCourses.length;
    this.totalElements = total;
    const maxPageIndex = Math.max(0, Math.ceil(total / this.pageSize) - 1);
    if (this.currentPage > maxPageIndex) {
      this.currentPage = 0;
    }
  }

  onSearchChange(): void {
    this.searchMode = !!this.searchTerm;
    this.currentPage = 0; // Reset to first page when searching
    this.loadCourses();
  }

  onFilterChange(): void {
    this.searchMode = this.hasActiveFilters();
    this.currentPage = 0; // Reset to first page when filtering
    this.loadCourses();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filters = {};
    this.searchMode = false;
    this.currentPage = 0;
    this.loadCourses();
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    if (!this.searchMode) {
      this.loadCourses();
    }
  }

  onPageSizeChange(newPageSize: number): void {
    this.pageSize = newPageSize;
    this.currentPage = 0; // Reset to first page when changing page size
    if (!this.searchMode) {
      this.loadCourses();
    }
  }

  getPaginationInfo(): string {
    const total = this.searchMode ? this.filteredCourses.length : this.totalElements;
    if (total === 0) {
      return `0-0 of 0`;
    }
    const start = this.currentPage * this.pageSize + 1;
    const end = Math.min((this.currentPage + 1) * this.pageSize, total);
    return `${start}-${end} of ${total}`;
  }

  getTotalPages(): number {
    const total = this.searchMode ? this.filteredCourses.length : this.totalElements;
    return Math.ceil(total / this.pageSize);
  }

  goToFirstPage(): void {
    this.currentPage = 0;
    if (!this.searchMode) {
      this.loadCourses();
    }
  }

  goToPreviousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      if (!this.searchMode) {
        this.loadCourses();
      }
    }
  }

  goToNextPage(): void {
    const totalPages = this.getTotalPages();
    if (this.currentPage < totalPages - 1) {
      this.currentPage++;
      if (!this.searchMode) {
        this.loadCourses();
      }
    }
  }

  goToLastPage(): void {
    const totalPages = this.getTotalPages();
    this.currentPage = totalPages - 1;
    if (!this.searchMode) {
      this.loadCourses();
    }
  }

  getPageNumbers(): (number | string)[] {
    const totalPages = this.getTotalPages();
    const currentPage = this.currentPage;
    const pageNumbers: (number | string)[] = [];

    if (totalPages <= 7) {
      // Show all pages if 7 or fewer
      for (let i = 0; i < totalPages; i++) {
        pageNumbers.push(i);
      }
    } else {
      // Always show first page
      pageNumbers.push(0);

      if (currentPage <= 3) {
        // Show first 5 pages + ellipsis + last page
        for (let i = 1; i <= 4; i++) {
          pageNumbers.push(i);
        }
        pageNumbers.push('...');
        pageNumbers.push(totalPages - 1);
      } else if (currentPage >= totalPages - 4) {
        // Show first page + ellipsis + last 5 pages
        pageNumbers.push('...');
        for (let i = totalPages - 5; i < totalPages; i++) {
          pageNumbers.push(i);
        }
      } else {
        // Show first page + ellipsis + current-1, current, current+1 + ellipsis + last page
        pageNumbers.push('...');
        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
          pageNumbers.push(i);
        }
        pageNumbers.push('...');
        pageNumbers.push(totalPages - 1);
      }
    }

    return pageNumbers;
  }

  goToPage(page: number | string): void {
    if (typeof page === 'number' && page !== this.currentPage) {
      this.currentPage = page;
      if (!this.searchMode) {
        this.loadCourses();
      }
    }
  }

  // Returns the list of courses to display on the current page
  getDisplayedCourses(): Course[] {
    if (this.searchMode) {
      const start = this.currentPage * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredCourses.slice(start, end);
    }
    return this.filteredCourses;
  }

  navigateToCourse(courseId: string): void {
    this.router.navigate(['/courses/view', courseId]);
  }

  editCourse(courseId: string): void {
    this.router.navigate(['/courses/edit', courseId]);
  }

  addNewCourse(): void {
    this.router.navigate(['/courses/add']);
  }

  deleteCourse(courseId: string): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Course',
        message: 'Are you sure you want to delete this course? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.courseService.deleteCourse(courseId).subscribe({
          next: () => {
            this.snackBar.open('Course deleted successfully', 'Close', {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top'
            });
            this.loadCourses();
          },
          error: (error) => {
            console.error('Error deleting course:', error);
            this.snackBar.open('Error deleting course', 'Close', {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top'
            });
          }
        });
      }
    });
  }

  getRandomCourseImage(course: Course): string {
    // Generate a consistent design pattern based on course name
    const seed = this.hashCode(course.name + course.id);
    const designIndex = Math.abs(seed) % 12; // 12 different design patterns
    return this.getDesignPattern(designIndex);
  }

  private getDesignPattern(index: number): string {
    const patterns = [
      // Gradient patterns
      'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
      'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
      'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
      'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
      'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
      'linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%)',
      'linear-gradient(135deg, #ff8a80 0%, #ff80ab 100%)',
      'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)'
    ];
    return patterns[index];
  }

  private hashCode(str: string): number {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
  }
}
