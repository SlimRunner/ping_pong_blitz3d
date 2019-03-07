; Program:		Structured design
; Objective:	Plays a 1 player ping pong
; Programmer:	Mario D. Flores
; Date:			Thu, Feb 28, 2019

;Functions *********************************

;Sets starting speed of the pong
Function StartSpeed(speed#, direction#)
	x_speed = speed * Cos(direction)
	y_speed = speed * Sin(direction)
End Function

;updates the location of the pong based on speed
Function UpdateLoc()
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
			
			StartSpeed(PONG_START_SPEED, speed_vector)
		End If
	End If
	
	;determines if the pong has hit the top or bottom edge
	If y_loc >= (VP_HEIGHT - BALL_HEIGHT) Or y_loc <= 0 Then
		y_speed = -y_speed
	End If
End Function

;updates the location of the bumpers
Function UpdateBumper()
	Rect 0,bumper_loc,BUMPER_WIDTH,BUMPER_HEIGHT,1
	Rect VP_WIDTH - BUMPER_WIDTH, bumper_loc,BUMPER_WIDTH,BUMPER_HEIGHT,1
	
	bumper_loc = MouseY() - BUMPER_HEIGHT/2
End Function

;resets the game to an initial state
Function ResetGame()
	Color 255, 0, 0
	Text VP_WIDTH/2, VP_HEIGHT/2, "Game Over", True, True
	Flip
	Delay 1000
	StartSpeed(PONG_START_SPEED, Rnd(-45,45))
	x_loc# = 320
	y_loc# = 240
	Color 255, 255, 255
End Function

;Calculates the direction of a vector starting at 0
Function GetAngle#()
	retval# = 0
	If x_speed < 0 Then
		Return (ATan(y_speed / x_speed) + 180)
	Else
		If x_speed = 0 Then
			If y_speed = 0 Then
				Return 0
			End If
			
			If y_speed > 0 Then
				Return 270 ;180 * 1.5
			Else
				Return 90 ;180 / 2
			EndIf
		Else
			If retval < 0 Then
				Return ATan(y_speed / x_speed) + 360
			Else
				Return ATan(y_speed / x_speed)
			End If
		End If
	End If
End Function

;Variables *********************************

;viewport properties
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
;StartSpeed(PONG_START_SPEED, Rnd(-45,45))
StartSpeed(PONG_START_SPEED, 22.5)

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
ShowPointer

End