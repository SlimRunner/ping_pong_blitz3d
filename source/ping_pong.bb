; Program:		Structured design
; Objective:	Plays a 1 player ping pong
; Programmer:	Mario D. Flores
; Date:			Thu, Feb 28, 2019

;Functions *********************************

;Sets starting speed of the pong
Function SetPongVector(speed#, direction#)
	;simple vector transformation from magnitude-direction to components
	x_speed = speed * Cos(direction)
	y_speed = speed * Sin(direction)
End Function

;updates the location of the pong based on speed
Function UpdateLoc()
	;location + speed
	x_loc = x_loc + x_speed
	y_loc = y_loc + y_speed
	
	;determines if the pong has hit the left or right edge
	If x_loc >= (VP_WIDTH - BALL_WIDTH - BUMPER_WIDTH) Or x_loc <= BUMPER_WIDTH Then
		x_speed = -x_speed
		
		;determines if the pong hit the bumper or the edge
		If (y_loc+8) <= bumper_loc Or (y_loc+8) >= (bumper_loc+bumper_height)
			;if the edge was hit, restart the game
			ResetGame()
		Else
			;if the bumper was hit, slight change of direction
			If Rnd(0,1) Then
				speed_vector# = GetAngle() + 8
			Else
				speed_vector# = GetAngle() - 8
			End If
			
			;changes slightly the pong direction
			SetPongVector(PONG_START_SPEED, speed_vector)
		End If
	End If
	
	;determines if the pong has hit the top or bottom edge
	If y_loc >= (VP_HEIGHT - BALL_HEIGHT) Or y_loc <= 0 Then
		y_speed = -y_speed
	End If
End Function

;updates the location of the bumpers
Function UpdateBumper()
	;uses the mouse location to set the bumper location
	bumper_loc = MouseY() - BUMPER_HEIGHT/2
	
	;draws bumpers
	Rect 0,bumper_loc,BUMPER_WIDTH,BUMPER_HEIGHT,1
	Rect VP_WIDTH - BUMPER_WIDTH, bumper_loc,BUMPER_WIDTH,BUMPER_HEIGHT,1
End Function

;resets the game to an initial state
Function ResetGame()
	;prints "game over" in red
	Color 255, 0, 0
	Text VP_WIDTH/2, VP_HEIGHT/2, "Game Over", True, True
	;makes sure the text is rendered before the game is paused
	Flip
	
	;pause to let the user read the message
	Delay 1000
	
	;reset pong to initial state
	SetPongVector(PONG_START_SPEED, Rnd(-45,45))
	x_loc# = 320
	y_loc# = 240
	Color 255, 255, 255
End Function

;Calculates the direction of a vector starting at 0
Function GetAngle#()
	;this function is also known as Atan2
	If x_speed < 0 Then
		 ;2nd and 3rd quadrants
		Return (ATan(y_speed / x_speed) + 180)
	Else
		;1nd and 4th quadrants
		
		If x_speed = 0 Then
			;special cases
			If y_speed = 0 Then
				;everything is just 0
				Return 0
			End If
			
			;vector is colinear to y
			If y_speed > 0 Then
				;lower quadrant (+y)
				Return 270
			Else
				;upper quadrant (-y)
				Return 90
			End If
		Else
			;1nd and 4th quadrants
			Return ATan(y_speed / x_speed)
			;note that if the vector resides in the 4th quadrant, the
			;arc tangent will return a negative number that, although
			;is mathematically correct, is not intuitive if it were to
			;be printed. To fix that a simple if should suffice.
		End If
	End If
End Function

;Variables *********************************

;CONSTANTS
Const VP_WIDTH = 640
Const VP_HEIGHT = 480
Const BUMPER_HEIGHT# = 48
Const BUMPER_WIDTH# = 5

Const BALL_WIDTH# = 16
Const BALL_HEIGHT# = 16

Const PONG_START_SPEED# = 1.8

;pong properties
Global x_speed# = 0
Global y_speed# = 0
Global x_loc# = 320
Global y_loc# = 240

;bumper properties
Global bumper_loc# = 240

;Startup ***********************************
;Turn on graphics mode
Graphics VP_WIDTH, VP_HEIGHT, 32, 2

;seeding the random value
SeedRnd MilliSecs()

;setting font
myFont = LoadFont("Arial", 24)
SetFont myFont

;start direction of ball
SetPongVector(PONG_START_SPEED, Rnd(-45,45))

;Hides pointer
HidePointer

; Create new empty graphic to store our circle in
gfxCircle=CreateImage( BALL_WIDTH, BALL_HEIGHT)

; Set drawing operations to point to our new empty graphic
SetBuffer ImageBuffer(gfxCircle)
;Set Color
Color 255,255,255

; Draw the circle image
Oval 0, 0, BALL_WIDTH, BALL_HEIGHT, 1

; Let's not forget to put the drawing buffer back!
SetBuffer BackBuffer()

;Loop **************************************
While Not KeyHit(1)
	;Logic *************************************
	UpdateLoc()

	;Drawing Output ****************************
	Cls
	UpdateBumper();draws bumper and updates info with key input
	DrawBlock gfxCircle, x_loc, y_loc
Flip
Wend

;Frees previously loaded font
FreeFont myFont

;Shows pointer again
ShowPointer

End